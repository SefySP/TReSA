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

public class Indexer
{
    private IndexWriter writer;

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

            String places = getString(stringBuilder, "<PLACES>", "</PLACES>");
            String people = getString(stringBuilder, "<PEOPLE>", "</PEOPLE>");
            String title = getString(stringBuilder, "<TITLE>", "</TITLE>");
            String body = getString(stringBuilder, "<BODY>", "</BODY>");

            EnglishStemmer stemmer = new EnglishStemmer();
            String[] words = body.split("[ .,]+");
            stringBuilder = new StringBuilder();
            for (String word : words)
            {
                stemmer.setCurrent(word);
                stemmer.stem();
                stringBuilder.append(stemmer.getCurrent()).append(" ");
            }
            body = stringBuilder.toString();


            Field placesField = new Field(LuceneConstants.PLACES, places, TextField.TYPE_STORED);
            Field peopleField = new Field(LuceneConstants.PEOPLE, people, TextField.TYPE_STORED);
            Field titleField = new Field(LuceneConstants.TITLE, title, TextField.TYPE_STORED);
            Field bodyField = new Field(LuceneConstants.BODY, body, TextField.TYPE_STORED);

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

    private String getString(StringBuilder stringBuilder, String tagStart, String tagEnd)
    {
        int placesStart = stringBuilder.toString().trim().indexOf(tagStart) + tagStart.length();
        int placesEnd = stringBuilder.toString().trim().indexOf(tagEnd);

//        System.out.println(placesStart + " " + placesEnd);
        return stringBuilder.substring(placesStart, placesEnd);
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
