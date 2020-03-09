package olnow.phmobile;

import javax.persistence.*;

@Entity
@Table(name = "import_template")
public class ImportTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idimport_template;

    @Column private String name;
    @Column private String template;

    public ImportTemplate() {}

    public ImportTemplate(String name, String template) {
        this.name = name;
        this.template = template;
    }

    public int getIdimport_template() {
        return idimport_template;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
