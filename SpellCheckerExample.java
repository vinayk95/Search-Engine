package com.Lucene;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.util.List;
import java.util.ArrayList;



public class SpellCheckerExample {

    public static String prefixPath = "";
//    private String indexPath = "";
    private String indexPath = "C:\\Users\\Vinay\\Desktop\\vinay\\IRS\\Code\\index";

    public SpellCheckerExample(String indexPath) {
    	this.indexPath = indexPath;

    }

    public List<Article> Search(List<String> queryString, int pageIndex, int pageSize) throws Exception {
        //prepare the searcher
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(prefixPath+indexPath)));
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);

		//single field search on the "content" field
//		QueryParser parser = new QueryParser("content", analyzer);
        //change the UTF-8 encoded string to binary string: query
//		Query query = parser.parse(queryString);

        //multiple field search on the "title", "author", "year", "content" fields
        //the field ahead will be with higher priority
        String[] fields={"title"};
        if ( queryString.size() < fields.length ) {
            int gap = fields.length - queryString.size();
            for (int i = 0; i < gap; i++) {
                queryString.add("");
            }
        };
        MultiFieldQueryParser parser=new MultiFieldQueryParser(Version.LUCENE_48, fields, analyzer);
        String[] tempStr = queryString.toArray(new String[queryString.size()]);
        Query query = parser.parse(Version.LUCENE_48, tempStr, fields, analyzer);

   
        ScoreDoc lastSd = getLastScoreDoc(pageIndex, pageSize, query, searcher);

		//search and obtain top n records, it is an middle result: topDocs
        
		TopDocs topDocs = searcher.searchAfter(lastSd,query, pageSize);
//		System.out.println(topDocs.totalHits + " total matching documents");
		//score and sort the top n records
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

//        ScoreDoc[] hits = searcher.search(query, 10000000).scoreDocs;
        SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter("<span style=\'color:red\'><b>","</b></span>");
        SimpleFragmenter fragmenter = new SimpleFragmenter();
        fragmenter.setFragmentSize(100);  
        Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));
        highlighter.setTextFragmenter(fragmenter);  

        //define a list of Articles to store Index Records
        List<Article> articalList = new ArrayList<Article>();

        //use the middle results "scoreDocs" to obtain completed records in the Index Base
        for (ScoreDoc scoreDoc : scoreDocs) {
            float score = scoreDoc.score;// to obtain the score
            int docId = scoreDoc.doc; // to obtain the internal Document ID

            // use the internal Document ID to obtain a real Index Record from the Index Base
            Document doc = searcher.doc(docId);
            if (doc != null ) {
                // convert the Document(Index Record) to Article
                Article artical = new Article();
                artical.setDocId(docId);
                artical.setTotalhits(topDocs.totalHits);

                artical.setScore(score);

                try {
                    artical.setFilepath(doc.getField("filepath").stringValue());
                } catch (Exception e) {
                    artical.setFilepath("");
                }


                try {

//                     String title = reader.document(docId).get("title");
// //                    System.out.println( "The title is "+ title);
//                   
//                     TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), docId, "title",reader.document(docId), analyzer);
//                     title =  highlighter.getBestFragments(tokenStream, title, 3,"...");
//                     artical.setTitle(title);
                     artical.setTitle(doc.getField("title").stringValue());

                } catch (Exception e) {
                    artical.setTitle("");
                }

                try {
                    artical.setAuthor(doc.getField("author").stringValue());
                } catch (Exception e) {
                    artical.setAuthor("");
                }

                try {
                    artical.setYear(doc.getField("year").stringValue());
                } catch (Exception e) {
                    artical.setYear("");
                }


                try {
                    //here .........................................
                    @SuppressWarnings("deprecation")
                    // obtaining the related content with length 500
                    String content = reader.document(docId).get("content");
//                    System.out.println( "The content is "+ content);
                    
                    TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), docId, "content",reader.document(docId), analyzer);
                    content =  highlighter.getBestFragments(tokenStream, content, 8,"...");
                    artical.setContent(content);

                } catch (Exception e) {
                    artical.setContent("");
                }

                try {
                    artical.setPublisher(doc.getField("publisher").stringValue());
                } catch (Exception e) {
                    artical.setPublisher("");
                }

                try {
                    artical.setCategory(doc.getField("category").stringValue());
                } catch (Exception e) {
                    artical.setCategory("");
                }

                //System.out.println(artical.toString());
                articalList.add(artical);

            }


        };
        reader.close();
        return articalList;

	}

    private ScoreDoc getLastScoreDoc(int pageIndex, int pageSize, Query query, IndexSearcher searcher) throws IOException {
    	if(pageIndex==1)return null;//如果是第一页就返回空
        int num = pageSize*(pageIndex-1);//获取上一页的最后数量
        TopDocs tds = searcher.search(query, num);
        return tds.scoreDocs[num-1];
	}

	public Article SearchDocId(String InputDocId) throws Exception {
        //prepare the searcher
    	Directory dir = FSDirectory.open(new File(indexPath));
		
		IndexReader reader = IndexReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);

        // use the internal Document ID to obtain a real Index Record from the Index Base
        Document doc = searcher.doc(Integer.parseInt(InputDocId));
        Article artical = new Article();
        if (doc != null ) {
            // convert the Document(Index Record) to Article
            artical.setDocId(Integer.parseInt(InputDocId));


            try {
                artical.setFilepath(doc.getField("filepath").stringValue());
            } catch (Exception e) {
                artical.setFilepath("");
            }


            try {

                artical.setTitle(doc.getField("title").stringValue());

            } catch (Exception e) {
                artical.setTitle("");
            }

            try {
                artical.setAuthor(doc.getField("author").stringValue());
            } catch (Exception e) {
                artical.setAuthor("");
            }

            try {
                artical.setYear(doc.getField("year").stringValue());
            } catch (Exception e) {
                artical.setYear("");
            }


            try {

                artical.setContent(doc.getField("content").stringValue());

            } catch (Exception e) {
                artical.setContent("");
            }

            try {
                artical.setPublisher(doc.getField("publisher").stringValue());
            } catch (Exception e) {
                artical.setPublisher("");
            }

            try {
                artical.setCategory(doc.getField("category").stringValue());
            } catch (Exception e) {
                artical.setCategory("");
            }

            //System.out.println(artical.toString());

        }

        reader.close();
        return artical;

    }



}