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
        console.log("typeof this.setListeners =", typeof this.setListeners);


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
        } else console.log("No data in attribute!!!!!!")

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
                        background: '#008000',
                        border: '#FFF1D0',
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
                    iterations: 300,
                    fit: true
                }
            },
            interaction: {
                dragView: true,
                zoomView: true
            },
        };

       this.data = {nodes, edges};
       this.network = new vis.Network(container, this.data, options);

        // Event that signals the loading of the page (Can be used like OnStart())
        this.network.once("stabilizationIterationsDone", () => {
            this.network.setOptions({physics: false});


            document.querySelectorAll('.trade-item').forEach(item => {

                item.addEventListener('click', () => {
                    console.log("Clicou numa troca");
                    this.network.unselectAll();
                    console.log()
                    const id1 = item.getAttribute('P1');
                    const id2 = item.getAttribute('P2');
                    const id1main = item.getAttribute('P1Main');
                    const id2main = item.getAttribute('P2Main');

                    if (id1 && id2 && id1main && id2main) {
                        this.mergeNodes(id1,id2main);
                        this.mergeNodes(id2,id1main);
                    }
                });

                item.addEventListener('mouseover', () => {
                    const id1 = item.getAttribute('P1');
                    const id2 = item.getAttribute('P2');

                    if (id1 && id2) {
                        this.network.selectNodes([id1, id2]);
                    }
                });

                item.addEventListener('mouseout', () => {
                    this.network.unselectAll();
                });
            });

            console.log("Physics disabled after stabilization");
            if (typeof this.$server !== 'undefined') {
                this.$server.graphFinishedLoading();
            }




        });


    }

    set graphData(value) {
        this.setAttribute('graphData', value);
    }

    get graphData() {
        return this.getAttribute('graphData');
    }


    mergeNodes(node1Id, node2Id) {
        // Garante que a física está desligada para que possas animar
        this.network.setOptions({ physics: false });

        // Usa getPositions para garantir que tens posições fixas
        const positions = this.network.getPositions([node1Id, node2Id]);

        const node1 = {
            ...this.data.nodes.get(node1Id),
            ...positions[node1Id]
        };
        const node2 = {
            ...this.data.nodes.get(node2Id),
            ...positions[node2Id]
        };

        const targetX = (node1.x + node2.x) / 2;
        const targetY = (node1.y + node2.y) / 2;

        let steps = 30;
        let currentStep = 0;

        // Interpolation
        const dx1 = (targetX - node1.x) / steps;
        const dy1 = (targetY - node1.y) / steps;
        const dx2 = (targetX - node2.x) / steps;
        const dy2 = (targetY - node2.y) / steps;

        const interval = setInterval(() => {
            currentStep++;

            this.data.nodes.update([
                { id: node1Id, x: node1.x + dx1 * currentStep, y: node1.y + dy1 * currentStep, fixed: true },
                { id: node2Id, x: node2.x + dx2 * currentStep, y: node2.y + dy2 * currentStep, fixed: true }
            ]);

            this.network.redraw();

            if (currentStep >= steps) {
                clearInterval(interval);


                const area1 = Math.exp(node1.value);
                const area2 = Math.exp(node2.value);
                const mergedArea = area1 + area2;
                const mergedValue = Math.log(mergedArea);
                console.log(node1.label);
                console.log(node2.label);

                const mergedNodeId = `merged_${node1Id}_${node2Id}`;
                this.data.nodes.add({
                    id: mergedNodeId,
                    value: mergedValue,
                    label: node2.label,
                    title: (node1.title || '') + "\n\n" + (node2.title || ''),
                    x: targetX,
                    y: targetY,
                    fixed: true
                });

                this.data.edges.forEach(edge => {
                    if (edge.from === node1Id || edge.from === node2Id) {
                        this.data.edges.update({ id: edge.id, from: mergedNodeId });
                    }
                    if (edge.to === node1Id || edge.to === node2Id) {
                        this.data.edges.update({ id: edge.id, to: mergedNodeId });
                    }
                });

                // Remover arestas que agora ligam o nó a si próprio
                this.data.edges.forEach(edge => {
                    if (edge.from === edge.to) {
                        this.data.edges.remove({ id: edge.id });
                    }
                });

                this.data.nodes.remove([node1Id, node2Id]);
                this.network.redraw();
            }
        }, 15);
    }


}
customElements.define('graph-viewer', GraphViewer);

