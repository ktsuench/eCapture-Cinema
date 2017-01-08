package hv.com.projectpowerade;

import java.util.ArrayList;

/**
 * Created by brentmarks on 2017-01-08.
 */

public class Genre {

    private String name;
    private int id;
    private int imgfile;
    private ArrayList<Genre> subCategories;

    public Genre(String name, int id, int imgfile){
        this.name = name;
        this.id = id;
        this.imgfile = imgfile;
        subCategories = null;
    }
    public Genre(String name, int id, int imgfile, ArrayList<Genre> subCategories){
        this.name = name;
        this.id = id;
        this.imgfile = imgfile;
        this.subCategories = subCategories;
    }

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return id;
    }

    public void setImgfile(int imgfile) { this.imgfile = imgfile; }
    public int getImgfile () { return this.imgfile; }

    public ArrayList<Genre> getSubCategories(){
        return subCategories;
    }

}