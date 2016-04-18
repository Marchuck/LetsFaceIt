package pl.test.face.facetest.thirdPartyApis;

/**
 * @author Lukasz Marczak
 * @since 18.04.2016.
 */
public class SwapiCharacter {
    public String name;
    public String mass;
    public String height;
    public String gender;

    @Override
    public String toString() {
        return name + ", gender: " + gender + ", mass: " + mass + ", height: " + height;
    }
}
