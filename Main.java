public class Main {

    public static void main(String[] args) {

        ServeiXML srv = new ServeiXML();

        Cataleg cataleg =
            srv.carregarCataleg("dades/ESDEVENIMENTS.xml");

        System.out.println(
            "Recintes: " +
            cataleg.getRecintes().size()
        );

        System.out.println(
            "Esdeveniments: " +
            cataleg.getEsdeveniments().size()
        );
    }
}