package com.Lucene;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.util.Version;

import java.io.StringReader;

// From chapter 8
public class HighlightIt {

  public static void main(String[] args) throws Exception {
        String searchText = "fox";      
        
        String text = "The quick brown fox jumps over the lazy dog";
    QueryParser parser = new QueryParser(Version.LUCENE_48,"field", new StandardAnalyzer(Version.LUCENE_48));
    
    Query query = parser.parse("fox");                           

    TokenStream tokens = new StandardAnalyzer(Version.LUCENE_48) 
        .tokenStream("field", new StringReader(text));      
    
    

    QueryScorer scorer = new QueryScorer(query, "field");                
    
    Fragmenter fragmenter = new SimpleFragmenter(10);
    
    Highlighter highlighter = new Highlighter(scorer);
    highlighter.setTextFragmenter(fragmenter);
                                                 
  }


}