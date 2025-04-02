import 'leaflet/dist/leaflet.css';
import L from 'leaflet';

class LeafletMapViewer extends HTMLElement {
    connectedCallback() {
        this.style.height = "100%";
        this.style.width = "100%";

        const container = document.createElement('div');
        container.style.height = '100%';
        container.style.width = '100%';
        container.id = "leaflet-map";
        this.appendChild(container);

        const rawData = this.geoData || this.getAttribute('geoData');
        if (!rawData) {
            console.error("Sem dados geoData recebidos.");
            return;
        }

        const geo = JSON.parse(rawData);
        const map = L.map(container).setView([32.7607, -16.9595], 12);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 18,
            attribution: '&copy; OpenStreetMap contributors'
        }).addTo(map);

        geo.features.forEach((feature) => {
            const polygon = L.polygon(feature.coordinates, {
                color: '#F66435',
                weight: 2,
                fillOpacity: 0.5
            }).addTo(map);

            polygon.bindPopup(`
                <strong>Proprietário:</strong> ${feature.owner}<br>
                <strong>Área:</strong> ${feature.area} m²<br>
                <strong>Freguesia:</strong> ${feature.freguesia}
            `);
        });
    }

    set geoData(value) {
        this.setAttribute('geoData', value);
    }

    get geoData() {
        return this.getAttribute('geoData');
    }
}

customElements.define('leaflet-map-viewer', LeafletMapViewer);
