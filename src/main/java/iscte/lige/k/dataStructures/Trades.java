package iscte.lige.k.dataStructures;

import java.util.Objects;

public class Trades {
    public Owner owner1;
    public Owner owner2;
    public Property owner1propertys;
    public Property owner2propertys;
    public int tradeId;
    public int nota;

    public Trades(Owner owner1, Owner owner2, Property p1, Property p2, int tradeId) {
        this.owner1 = owner1;
        this.owner2 = owner2;
        this.owner1propertys = p1;
        this.owner2propertys = p2;
        this.tradeId = tradeId;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }
@Override
public String toString() {
        return ("Trade nrº: "+tradeId+" , Owner1: "+owner1.toString()+" , Owner2: "+owner2.toString()+" , Property: "+owner1propertys.toString()+" , Property: "+owner2propertys.toString());
}
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;  // Se for a mesma instância, retorna true
        if (obj == null || getClass() != obj.getClass()) return false; // Se for null ou de outra classe, retorna false

        Trades other = (Trades) obj;

        // Verifica se a troca é a mesma, independentemente da ordem dos Owners
        boolean sameOrder = owner1.getName().equals(other.owner1.getName()) &&
                owner2.getName().equals(other.owner2.getName()) &&
                owner1propertys.getParcelaId() == other.owner1propertys.getParcelaId() &&
                owner2propertys.getParcelaId() == other.owner2propertys.getParcelaId();

        boolean swappedOrder = owner1.getName().equals(other.owner2.getName()) &&
                owner2.getName().equals(other.owner1.getName()) &&
                owner1propertys.getParcelaId() == other.owner2propertys.getParcelaId() &&
                owner2propertys.getParcelaId() == other.owner1propertys.getParcelaId();

        return sameOrder || swappedOrder;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                owner1.getName(), owner2.getName(),
                Math.min((int)Double.parseDouble(owner1propertys.getParcelaId()), (int)Double.parseDouble(owner2propertys.getParcelaId())),
                Math.max((int)Double.parseDouble(owner1propertys.getParcelaId()), (int)Double.parseDouble(owner2propertys.getParcelaId())));
    }
}
