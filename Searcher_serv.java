package Search_Engine;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;


@WebServlet("/Searcher_serv")
public class Searcher_serv extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public void search(HttpServletRequest request, HttpServletResponse response) {
			String index = "E:/temp/lucene6index";
			String key_term = request.getParameter("key_term");
			try {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter write = response.getWriter();
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new StandardAnalyzer();
			QueryParser parser = new QueryParser("contents", analyzer);
			Query query = parser.parse(key_term);
			TopDocs results = searcher.search(query, 5);
			write.println(results.totalHits + " total matching documents");
			for (int i = 0; i < 5; i++) {
				Document doc = searcher.doc(results.scoreDocs[i].doc);
				String path = doc.get("path");
				write.println((i + 1) + ". " + path);
				String title = doc.get("title");
				if (title != null) {
					write.println("   Title: " + doc.get("title"));
				}
			}
			reader.close();
		} 
			catch(Exception e) {
				System.out.println(e.getMessage());}
			}
    public Searcher_serv() {
        super();
        }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doGet(request, response);
		search(request, response);
		String name = request.getParameter("key_term");
		PrintWriter write = response.getWriter();
		write.println("<br>" + name);
	}

}
