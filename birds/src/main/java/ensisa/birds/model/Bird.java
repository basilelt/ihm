package ensisa.birds.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javafx.beans.property.*;
import javafx.scene.image.Image;

public class Bird {
    private StringProperty family = new SimpleStringProperty(this, "family", "");
    private StringProperty genus = new SimpleStringProperty(this, "genus", "");
    private StringProperty specie = new SimpleStringProperty(this, "specie", "");
    private StringProperty commonName = new SimpleStringProperty(this, "commonName", "");
    private StringProperty latinName = new SimpleStringProperty(this, "latinName", "");
    private StringProperty description = new SimpleStringProperty(this, "description", "");
    private StringProperty imagePath = new SimpleStringProperty(this, "imagePath", "");

    public String getFamily() {
        return family.get();
    }

    public StringProperty familyProperty() {
        return family;
    }

    public void setFamily(String family) {
        this.family.set(family);
    }

    public String getGenus() {
        return genus.get();
    }

    public StringProperty genusProperty() {
        return genus;
    }

    public void setGenus(String genus) {
        this.genus.set(genus);
    }

    public String getSpecie() {
        return specie.get();
    }

    public StringProperty specieProperty() {
        return specie;
    }

    public void setSpecie(String specie) {
        this.specie.set(specie);
    }

    public String getCommonName() {
        return commonName.get();
    }

    public StringProperty commonNameProperty() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName.set(commonName);
    }

    public String getLatinName() {
        return latinName.get();
    }

    public StringProperty latinNameProperty() {
        return latinName;
    }

    public void setLatinName(String latinName) {
        this.latinName.set(latinName);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getImagePath() {
        return imagePath.get();
    }

    public StringProperty imagePathProperty() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath.set(imagePath);
    }

    @JsonIgnore
    private Property<Image> image = new SimpleObjectProperty<>(this, "image");

    public Image getImage() {
        return image.getValue();
    }

    public Property<Image> imageProperty() {
        return image;
    }

    public void setImage(Image image) {
        this.image.setValue(image);
    }
}
