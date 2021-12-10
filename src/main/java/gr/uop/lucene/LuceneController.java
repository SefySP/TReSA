package gr.uop.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;

public class LuceneController
{
    public static final String INDEX_DIR = "src/main/resources/gr.uop.lucene/index";
    public static final String DATA_DIR = "src/main/resources/gr.uop.lucene/data";

    public TopDocs hits;

    public void createIndex() throws IOException
    {
        Indexer indexer = new Indexer(INDEX_DIR);

        int numIndexed;
        long startTime = System.currentTimeMillis();
        numIndexed = indexer.createIndex(DATA_DIR, new TextFileFilter());
        long endTime = System.currentTimeMillis();
        indexer.close();
        System.out.println(numIndexed + " File(s) indexed, time taken: " +
                (endTime - startTime) + " ms");
    }

    public void search(String searchQuery) throws IOException, ParseException
    {
        Searcher searcher = new Searcher(INDEX_DIR);

        long startTime = System.currentTimeMillis();
        hits = searcher.search(searchQuery);
        long endTime = System.currentTimeMillis();


        System.out.println(hits.totalHits + " documents found. Time :" + (endTime - startTime));
        System.out.println(getTopResults());

        searcher.close();
    }


    public String getTopResults() throws IOException
    {
        Searcher searcher = new Searcher(INDEX_DIR);
        StringBuilder topDocs = new StringBuilder();
        for (ScoreDoc scoreDoc: hits.scoreDocs)
        {
            Document doc = searcher.getDocument(scoreDoc);
            topDocs.append(doc.get(LuceneConstants.FILE_NAME)).append("\n");
        }
        return topDocs.toString();
    }
}
