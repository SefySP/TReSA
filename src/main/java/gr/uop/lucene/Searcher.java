package gr.uop.lucene;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.ScoreMode;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Searcher
{
    private final IndexSearcher indexSearcher;
    private final Directory indexDirectory;
    private final IndexReader indexReader;
    private final QueryParser queryParser;

    public Searcher(String indexDirectoryPath) throws IOException
    {
        Path indexPath = Paths.get(indexDirectoryPath);
        indexDirectory = FSDirectory.open(indexPath);
        indexReader = DirectoryReader.open(indexDirectory);
        indexSearcher = new IndexSearcher(indexReader);
        queryParser = new MultiFieldQueryParser(new String[]{LuceneConstants.BODY, LuceneConstants.PLACES,
                                                             LuceneConstants.TITLE,
                                                             LuceneConstants.PEOPLE},
                new EnglishAnalyzer(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET));
    }

    public TopDocs search(String searchQuery) throws IOException, ParseException
    {
        Query query = queryParser.parse(searchQuery);
        query.createWeight(indexSearcher, ScoreMode.COMPLETE, 0.5f);
        System.out.println("query: " + query.toString());
        return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
    }

    public Document getDocument(ScoreDoc scoreDoc) throws IOException
    {
        return indexSearcher.doc(scoreDoc.doc);
    }

    public void close() throws IOException
    {
        indexReader.close();
        indexDirectory.close();
    }
}
