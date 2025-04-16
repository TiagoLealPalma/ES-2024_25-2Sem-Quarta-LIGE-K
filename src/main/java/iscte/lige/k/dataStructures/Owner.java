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
        List<String> parcelas = new ArrayList<>();

        Area(Property property) {
            area = property.getArea();
            geometry = property.getGeometry();
            parcelas.add(property.getParcelaId());
        }

        public void addArea(Property property){
            area += property.getArea();
            geometry.union(property.getGeometry());
            parcelas.add(property.getParcelaId());
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

    public double calculateAvgArea(){
        // Checks for robustness
        if (numOfProperties == 0) return 0;


        List<Property> uniqueProperties = new ArrayList<>(); // Properties that are not neighbours of each other
        List<Area> jointProperties = new ArrayList<>(); // Properties next to one another
        List<Integer> notUnique = new ArrayList<>();

        // Unir terrenos vizinhos, se existirem
        for (int i = 0; i < properties.size(); i++) {
            // Verificar se propriedade ja faz parte de um grupo unido
            Area areaToEvaluate = null;
            for(Area area : jointProperties){ // Se sim, pega nessa grupo e avalia
                if(area.parcelas.contains(properties.get(i).getParcelaId())) {
                    areaToEvaluate = area;
                    //notUnique.add(i);
                }
            }

            if(areaToEvaluate == null){
                areaToEvaluate = new Area(properties.get(i));
            }

            // Guardar temporariamente a area para comparar se houve alteração no final
            Double temp = areaToEvaluate.area;

            for(int j = 0; j < properties.size(); j++){
                Property property = properties.get(j);
                if(areaToEvaluate.parcelas.contains(property.getParcelaId())) // Já foi avaliado e unido
                    continue;

                // Se ainda nao estiver unido e intersetar, adicionar propriedade a area
                if(areaToEvaluate.geometry.touches(property.getGeometry()) ||
                        areaToEvaluate.geometry.intersects(property.getGeometry())){
                    areaToEvaluate.addArea(property);
                    if(!notUnique.contains(i)) notUnique.add(i);
                    notUnique.add(j);
                }
            }
            // Adicionar as joint properties se a area inicial diferir desta
            if (temp != areaToEvaluate.area)
                if(!jointProperties.contains(areaToEvaluate))
                    jointProperties.add(areaToEvaluate);
        }

        for (int i = 0; i < properties.size(); i++) {
            if(!notUnique.contains(i))
                uniqueProperties.add(properties.get(i));
        }

        // Calcular media
        double sum = 0;
        for (Property property : uniqueProperties) {
            sum += property.getArea();
        }
        for (Area area: jointProperties){
            sum += area.area;
        }

        //if(notUnique.size() > 0) // For DEBUG purposes
            //System.err.println("Owner tem " + notUnique.size() + " propriedades unidas " + this.name + " " + jointProperties.size());

        return sum/(uniqueProperties.size() + jointProperties.size());
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
