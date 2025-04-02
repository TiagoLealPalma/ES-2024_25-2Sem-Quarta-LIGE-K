package iscte.lige.k.dataStructures;

import org.locationtech.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.List;

public class Owner {
    private String name;
    private List<Property> properties;
    private int numOfProperties = 0;

    private class Area{
        double area;
        Geometry geometry;
        List<Integer> parcelas = new ArrayList<>();

        Area(Property property) {
            area = property.getArea();
            geometry.union(property.getGeometry());
            parcelas.add(Integer.parseInt(property.getParcelaId()));
        }

        public void addArea(Property property){
            area += property.getArea();
            geometry.union(property.getGeometry());
            parcelas.add(Integer.parseInt(property.getParcelaId()));
        }
    }

    public Owner (String name) {
        this.name = name;
        this.properties = new ArrayList<>();
    }

    public Owner (String name, Property property) {
        this.name = name;
        this.properties = new ArrayList<>();
        this.properties.add(property);
        numOfProperties++;
    }

    public void addProperty(Property property) {
        if (property == null)
            throw new IllegalArgumentException("Property cannot be null");

        properties.add(property);
        numOfProperties++;
    }

    public void calculateAvgArea(){
        List<Property> uniqueProperties = new ArrayList<>(); // Properties that are not neighbours of each other
        List<Area> jointProperties = new ArrayList<>(); // Properties next to one another

        // Unir terrenos vizinhos, se existirem
        for (int i = 0; i < properties.size(); i++) {
            // Verificar se propriedade ja faz parte de um grupo unido
            Area areaToEvaluate = null;
            for(Area area : jointProperties){ // Se sim, pega nessa grupo e avalia
                if(area.parcelas.contains(properties.get(i).parcelaId))
                    areaToEvaluate = area;
            }

            if(areaToEvaluate == null)
                areaToEvaluate = new Area(properties.get(i));

            for(Property property : properties){
                if(areaToEvaluate.parcelas.contains(property.getParcelaId())) // Já foi avaliado e unido
                    continue;

                if(areaToEvaluate.geometry.touches(property.getGeometry()) ||
                        areaToEvaluate.geometry.intersects(property.getGeometry())){
                    areaToEvaluate.addArea(property);
                }
            } // Adicionar as joint properties se a area inicial diferir desta
        }

        // Calcular media
        double sum = 0;
        for (Property property : uniqueProperties) {
            sum += property.getArea()/uniqueProperties.size();
        }
        for (Area area: jointProperties){
            sum += area.area / jointProperties.size();
        }
    }

    // Getters

    public String getName() {
        return name;
    }

    public List<Property> getProperties() { return properties; }

    public int getNumOfProperties() {
        return numOfProperties;
    }

    @Override
    public String toString() {
        return "Owner{" +
                "name='" + name + '\'' +
                ", numOfProperties=" + numOfProperties +
                '}';
    }
}
