package gr.uop;

public class ArticleData
{
    private String places;
    private String people;
    private String title;
    private String body;
    private final String fileName;
    private final String filePath;

    public ArticleData(String fileName, String filePath)
    {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public String getPlaces()
    {
        return places;
    }

    public void setPlaces(StringBuilder stringBuilder)
    {
        this.places = this.innerString(stringBuilder, "<PLACES>", "</PLACES>");
    }

    public String getPeople()
    {
        return people;
    }

    public void setPeople(StringBuilder stringBuilder)
    {
        this.people = this.innerString(stringBuilder, "<PEOPLE>", "</PEOPLE>");
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(StringBuilder stringBuilder)
    {
        this.title = this.innerString(stringBuilder, "<TITLE>", "</TITLE>");
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(StringBuilder stringBuilder)
    {
        this.body = this.innerString(stringBuilder, "<BODY>", "</BODY>");
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public String getFileName()
    {
        return fileName;
    }

    public String getFilePath()
    {
        return filePath;
    }

    private String innerString(StringBuilder stringBuilder, String tagStart, String tagEnd)
    {
        int placesStart = stringBuilder.toString().trim().indexOf(tagStart) + tagStart.length();
        int placesEnd = stringBuilder.toString().trim().indexOf(tagEnd);

        return stringBuilder.substring(placesStart, placesEnd);
    }

    @Override
    public String toString()
    {
        return "<PLACES>" + getPlaces() + "</PLACES>\n" + "<PEOPLE>" + getPeople() + "</PEOPLE>\n" + "<TITLE" +
                ">" + getTitle() + "</TITLE>\n" + "<BODY>" + getBody() + "</BODY>";
    }
}
