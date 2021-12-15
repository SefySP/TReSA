package gr.uop.lucene;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import gr.uop.ArticleData;

public class Indexer implements AutoCloseable
{
    private final IndexWriter writer;

    public Indexer(String indexDirectoryPath) throws IOException
    {
        Path indexPath = Paths.get(indexDirectoryPath);

        Directory indexDirectory = FSDirectory.open(indexPath);
        //create the indexer
        IndexWriterConfig config = new IndexWriterConfig(new EnglishAnalyzer(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET));
        writer = new IndexWriter(indexDirectory, config);
    }

    public void close() throws IOException
    {
        writer.close();
    }

    private Document getDocument(File file)
    {
        Document document = new Document();
        //index file contents
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String currentLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((currentLine = br.readLine()) != null)
            {
                stringBuilder.append(currentLine).append(" ");
            }

            ArticleData articleData = new ArticleData(file.getName(), file.getCanonicalPath());

            articleData.setPlaces(stringBuilder);
            articleData.setPeople(stringBuilder);
            articleData.setTitle(stringBuilder);
            articleData.setBody(stringBuilder);

            EnglishStemmer stemmer = new EnglishStemmer();
            String[] words = articleData.getBody().split("[ .,]+");
            stringBuilder = new StringBuilder();
            for (String word : words)
            {
                stemmer.setCurrent(word);
                stemmer.stem();
                stringBuilder.append(stemmer.getCurrent()).append(" ");
            }
            articleData.setBody(stringBuilder.toString());


            Field placesField = new Field(LuceneConstants.PLACES, articleData.getPlaces(), TextField.TYPE_STORED);
            Field peopleField = new Field(LuceneConstants.PEOPLE, articleData.getPeople(), TextField.TYPE_STORED);
            Field titleField = new Field(LuceneConstants.TITLE, articleData.getTitle(), TextField.TYPE_STORED);
            Field bodyField = new Field(LuceneConstants.BODY, articleData.getBody(), TextField.TYPE_STORED);

            Field fileNameField = new Field(LuceneConstants.FILE_NAME, file.getName(), StringField.TYPE_STORED);
            Field filePathField = new Field(LuceneConstants.FILE_PATH, file.getCanonicalPath(),
                    StringField.TYPE_STORED);

            document.add(placesField);
            document.add(peopleField);
            document.add(titleField);
            document.add(bodyField);
            document.add(fileNameField);
            document.add(filePathField);
        }
        catch (IOException ioException)
        {
            System.out.println(ioException.getMessage());
        }
        return document;
    }

    private void indexFile(File file) throws IOException
    {
        System.out.println("Indexing " + file.getCanonicalPath());
        Document document = getDocument(file);
        System.out.println(document.getField(LuceneConstants.BODY));
        writer.addDocument(document);
    }

    public int createIndex(String dataDirPath, FileFilter filter) throws
            IOException
    {
        //get all files in the data directory

        File[] files = new File(dataDirPath).listFiles();

        if (files != null)
        {
            for (File file : files)
            {
                if (!file.isDirectory()
                        && !file.isHidden()
                        && file.exists()
                        && file.canRead()
                        && filter.accept(file)
                )
                {
                    indexFile(file);
                }
            }
        }
        return writer.numRamDocs();

    }
}
