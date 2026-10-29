import java.time.LocalDateTime;
import java.util.ArrayList;

public class Esdeveniment {

    private String id;
    private String nom;
    private LocalDateTime dataHora;
    private String categoria;
    private String descripcio;
    private String imatge;
    private Recinte recinte;
    private ArrayList<TipusEntrada> tipusEntrades;

    public Esdeveniment(String id,
                        String nom,
                        LocalDateTime dataHora,
                        String categoria,
                        String descripcio,
                        String imatge,
                        Recinte recinte) {

        this.id = id;
        this.nom = nom;
        this.dataHora = dataHora;
        this.categoria = categoria;
        this.descripcio = descripcio;
        this.imatge = imatge;
        this.recinte = recinte;
        this.tipusEntrades = new ArrayList<>();
    }

    public void afegirTipusEntrada(TipusEntrada entrada) {
        tipusEntrades.add(entrada);
    }

    public String getId() {
        return id;
    }

    public ArrayList<TipusEntrada> getTipusEntrades() {
        return tipusEntrades;
    }
}