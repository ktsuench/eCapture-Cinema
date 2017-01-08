package hv.com.projectpowerade;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by domenic on 2017-01-08.
 */

public class Recommendations implements Serializable {

    public int id;
    public int pageNum;
    ArrayList<Result> results = new ArrayList<Result>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public ArrayList<Result> getResults() {
        return results;
    }

    public void setResults(ArrayList<Result> results) {
        this.results = results;
    }
}
