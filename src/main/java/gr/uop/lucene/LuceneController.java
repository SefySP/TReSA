package gr.uop.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LuceneController
{
    public static final String INDEX_DIR = "src/main/resources/gr.uop.lucene/index";
    public static final String DATA_DIR = "src/main/resources/gr.uop.lucene/data";

    public TopDocs hits;

    public void createIndexDir()
    {
        Path indexPath = Paths.get(LuceneController.INDEX_DIR);
        try
        {
            Files.createDirectory(indexPath);
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public boolean indexDirExists()
    {
        Path indexPath = Paths.get(LuceneController.INDEX_DIR);
        return Files.exists(indexPath);
    }

    public boolean isIndexDirEmpty()
    {
        File index = new File(INDEX_DIR);
        String[] entries = index.list();
        if(entries != null)
        {
            return entries.length == 0;
        }
        return false;
    }

    public void deleteIndexDir()
    {
        String[] entries = new File(INDEX_DIR).list();
        assert entries != null;
        for (String file : entries)
        {
            File currentFile = new File(new File(INDEX_DIR), file);
            try
            {
                Files.delete(currentFile.toPath());
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void createIndex()
    {
        try (Indexer indexer = new Indexer(INDEX_DIR))
        {
            int numIndexed;
            long startTime = System.currentTimeMillis();
            numIndexed = indexer.createIndex(DATA_DIR, new TextFileFilter());
            long endTime = System.currentTimeMillis();
            System.out.println(numIndexed + " File(s) indexed, time taken: " + (endTime - startTime) + " ms");
        }
        catch (IOException ioException)
        {
            ioException.printStackTrace();
        }

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
        for (ScoreDoc scoreDoc : hits.scoreDocs)
        {
            Document doc = searcher.getDocument(scoreDoc);
            topDocs.append(doc.get(LuceneConstants.FILE_NAME)).append("\n");
        }
        return topDocs.toString();
    }
}
