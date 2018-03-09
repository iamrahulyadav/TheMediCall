package themedicall.com.GetterSetter;

/**
 * Created by Muhammad Adeel on 2/22/2018.
 */

public class HomeCareRequiredGetterSetter {

    private String careRequiredImage ;
    private String careRequiredStatus ;
    private String careRequiredHours ;
    private String careRequiredRequestName ;
    private String careRequiredDescription ;
    private String careRequiredId ;
    private Double careRequiredDistance ;

    public HomeCareRequiredGetterSetter(String careRequiredImage, String careRequiredStatus, String careRequiredHours, String careRequiredRequestName, String careRequiredDescription, String careRequiredId , Double careRequiredDistance) {
        this.careRequiredImage = careRequiredImage;
        this.careRequiredStatus = careRequiredStatus;
        this.careRequiredHours = careRequiredHours;
        this.careRequiredRequestName = careRequiredRequestName;
        this.careRequiredDescription = careRequiredDescription;
        this.careRequiredId = careRequiredId;
        this.careRequiredDistance = careRequiredDistance;
    }


    public String getCareRequiredImage() {
        return careRequiredImage;
    }

    public void setCareRequiredImage(String careRequiredImage) {
        this.careRequiredImage = careRequiredImage;
    }

    public String getCareRequiredStatus() {
        return careRequiredStatus;
    }

    public void setCareRequiredStatus(String careRequiredStatus) {
        this.careRequiredStatus = careRequiredStatus;
    }

    public String getCareRequiredHours() {
        return careRequiredHours;
    }

    public void setCareRequiredHours(String careRequiredHours) {
        this.careRequiredHours = careRequiredHours;
    }

    public String getCareRequiredRequestName() {
        return careRequiredRequestName;
    }

    public void setCareRequiredRequestName(String careRequiredRequestName) {
        this.careRequiredRequestName = careRequiredRequestName;
    }

    public String getCareRequiredDescription() {
        return careRequiredDescription;
    }

    public void setCareRequiredDescription(String careRequiredDescription) {
        this.careRequiredDescription = careRequiredDescription;
    }

    public String getCareRequiredId() {
        return careRequiredId;
    }

    public void setCareRequiredId(String careRequiredId) {
        this.careRequiredId = careRequiredId;
    }

    public Double getCareRequiredDistance() {
        return careRequiredDistance;
    }

    public void setCareRequiredDistance(Double careRequiredDistance) {
        this.careRequiredDistance = careRequiredDistance;
    }
}
