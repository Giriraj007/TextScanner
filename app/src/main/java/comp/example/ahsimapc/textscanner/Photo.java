package comp.example.ahsimapc.textscanner;

import java.util.UUID;

public class Photo {

    private UUID uuid;
    Photo()
    {

        uuid=UUID.randomUUID();
    }




    public String getFileName()
    {

        return "IMG_"+uuid.toString()+".jpg";
    }


}
