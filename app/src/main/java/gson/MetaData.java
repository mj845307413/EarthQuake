package gson;

/**
 * Created by ws02 on 2016/1/7.
 */
public class MetaData {
    public long generated;
    public String url;
    public String title;
    public int status;
    public String api;
    public int count;

    public long getGenerated() {
        return generated;
    }

    public void setGenerated(long generated) {
        this.generated = generated;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "gson.MetaData{" +
                "generated=" + generated +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", api='" + api + '\'' +
                ", count=" + count +
                '}';
    }
}
