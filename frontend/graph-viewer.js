// This version uses CDN for easier testing
console.log("Script loaded: graph-viewer.js");

if (!window.visLoaded) {
    const visScript = document.createElement('script');
    visScript.src = 'https://unpkg.com/vis-network/standalone/umd/vis-network.min.js';
    visScript.onload = () => {
        window.visLoaded = true;
        console.log("vis-network library loaded successfully");
    };
    document.head.appendChild(visScript);
}

class GraphViewer extends HTMLElement {
    connectedCallback() {
        if (this.built) {
            console.log("Graph already built. Skipping re-initialization.");
            return;
        }
        console.log("GraphViewer component connected to DOM");

        this.style.height = "100%";
        this.style.width = "100%";
        this.style.border = "0px solid #ccc";

        const container = document.createElement('div');
        container.style.height = '100%';
        container.style.width = '100%';
        this.appendChild(container);

        if (typeof this.$server !== 'undefined') {
            console.log("Requesting data from server via startLoadingOnServer()");
            console.log(window.visLoaded + "visLoaded");
            this.$server.startLoadingOnServer();
        }

        // Check immediately if data is already present
        const initialRawData = this.graphData || this.getAttribute('graphData');
        if (initialRawData) {
            console.log("graphData was already present. Parsing immediately...");
            this.tryBuildGraph(initialRawData, container);
        }else console.log("No data in attribute!!!!!!")

        // Redundancy in case observer doesn't work
        this.lastGraphData = null;

        const checkGraphData = () => {
            if (this.built) return;

            const current = this.graphData || this.getAttribute('graphData');
            if (current && current !== this.lastGraphData) {
                console.log("graphData changed (via polling). Attempting to parse...");
                this.lastGraphData = current;
                this.tryBuildGraph(current, container);
                this.built = true;
            } else {
                setTimeout(checkGraphData, 500);
            }
        };
        checkGraphData();
    }

    tryBuildGraph(rawData, container) {
        let parsed;
        try {
            parsed = JSON.parse(rawData);
        } catch (e) {
            console.error("Error parsing JSON:", e);
            return;
        }

        const waitForVis = () => {
            if (window.visLoaded && window.vis) {
                console.log("Building graph now...");
                this.buildGraph(parsed, container);
            } else {
                setTimeout(waitForVis, 50);
            }
        };

        waitForVis();
    }

    buildGraph(parsed, container) {
        const nodes = new vis.DataSet(parsed.nodes);
        const edges = new vis.DataSet(parsed.edges);

        const options = {
            nodes: {
                shape: 'dot',
                scaling: {
                    min: 10,
                    max: 50,
                },
                color: {
                    background: '#F66435',
                    border: '#F66435',
                    highlight: {
                        background: '#FFF1D0',
                        border: '#F66435',
                    }
                },
                font: {
                    color: 'rgb(255, 241, 208)',
                    face: 'Inter',
                    size: 20,
                }
            },
            edges: {
                color: '#F66435',
                width: 1.5,
                smooth: {
                    type: 'dynamic'
                }
            },
            layout: {
                improvedLayout: false
            },
            physics: {
                enabled: true,
                solver: 'forceAtlas2Based',
                forceAtlas2Based: {
                    gravitationalConstant: -50,
                    centralGravity: 0.01,
                    springLength: 100,
                    springConstant: 0.08,
                    damping: 0.4
                },
                stabilization: {
                    iterations: 150,
                    fit: true
                }
            },
            interaction: {
                dragView: true,
                zoomView: true
            }
        };

        const data = { nodes, edges };
        const network = new vis.Network(container, data, options);

        network.once("stabilizationIterationsDone", () => {
            network.setOptions({ physics: false });
            console.log("Physics disabled after stabilization");

            if (typeof this.$server !== 'undefined') {
                this.$server.graphFinishedLoading();
            }

            // Force the graph to fit the screen
            network.fit({
                animation: {
                    duration: 500,
                    easingFunction: "easeInOutQuad"
                }
            });
        });
    }

    set graphData(value) {
        this.setAttribute('graphData', value);
    }

    get graphData() {
        return this.getAttribute('graphData');
    }
}

customElements.define('graph-viewer', GraphViewer);
