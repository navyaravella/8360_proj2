import java.util.*;
import java.io.*;

public class Probabilistic {

	private Document[] docs;
	private String[] terms;  
        private int R, N;       
	int T;                   	
	private int[] r, n;      
	
	public static void main(String[] args) {
		Probabilistic probabilistic = new Probabilistic();
		probabilistic.buildDocuments("InputDocs.txt");
		
		int N = probabilistic.docs.length;
		int T = probabilistic.terms.length;
		double[] sim = new double[N];
		int[] id = new int[N];
                
                System.out.println("Total number of processed documents: " + N);
                //System.out.println("Total number of distinct words stored in the inverted index: " + T+"\n");
				
		FileInputStream fin = null;
		try {
                    fin = new FileInputStream("query.txt");
		} catch (FileNotFoundException e) {
                    e.printStackTrace();
		}
		Scanner sc = new Scanner(fin);
		
		for (int i = 0; i < N; i++)
			id[i] = i;
		
		while (sc.hasNext()) {
			String line = sc.nextLine();
			Document query = new Document(line);
			
			Document[] relDocs = new Document[0];
			
			for (int i = 0; i < 3; i++) {
				probabilistic.initModel(relDocs);
				for (int j = 0; j < N; j++)
					sim[j] = probabilistic.similarity(query, j);
				for (int j = 0; j < N; j++)
					for (int k = j + 1; k < N; k++)
						if (sim[id[j]] < sim[id[k]]) {
							int temp = id[j];
							id[j] = id[k];
							id[k] = temp;
						}
				relDocs = new Document[10];
				for (int j = 0; j < 10; j++)
					relDocs[j] = probabilistic.docs[id[j]];
			}
			
			System.out.println("Query: " + query);
			System.out.println("Rank \t"+"Document");
                        System.out.println("-----------------------------------------------------------------------------------------");
                        for (int i = 0; i < 3; i++) {
                            System.out.println((i + 1) + ". "+ "\t"+ probabilistic.docs[id[i]]);
                            //System.out.println((i + 1) + ". " + String.format("%.4f", sim[id[i]]) + " \"" + probabilistic.docs[id[i]] + "\"");
			}
                        System.out.println("-----------------------------------------------------------------------------------------");
			System.out.println("");
		}
	}
	public void buildDocuments(String docFile) {
		FileInputStream fin = null;
		try {
                    fin = new FileInputStream(docFile);
		} catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
		
		Set<String> terms = new HashSet<String>();
		List<Document> docs = new LinkedList<Document>();
                
                Scanner sc = new Scanner(fin);
		while (sc.hasNext()) {
                    String line = sc.nextLine();
                    Document doc = new Document(line);
                    for (String word : doc.getTerms()) {
			terms.add(word);
                    }
                    docs.add(doc);
		}
		
		this.terms = terms.toArray(new String[0]);
		this.docs = docs.toArray(new Document[0]);
	}
	public void initModel(Document[] relDocs) {
                N = docs.length;
                R = relDocs.length;
		T = terms.length;
		r = new int[T];
		n = new int[T];
		for (int i = 0; i < T; i++) {
			r[i] = n[i] = 0;
			for (int j = 0; j < R; j++){
                            if (relDocs[j].containsTerm(terms[i])){
				r[i]++;
                            }
                        }    
                for (int j = 0; j < N; j++){ 
                    if (docs[j].containsTerm(terms[i])){
			n[i]++;
                    } 
                }        
            }
	}

	public double similarity(Document query, int j) {
		double sim = 0;
		for (int i = 0; i < T; i++) {
			if (!docs[j].containsTerm(terms[i])) continue;
                        if (!query.containsTerm(terms[i])) continue;
			sim = sim + Math.log((r[i] + 0.5) / (R - r[i] + 0.5) * (N - n[i] - R + r[i] + 0.5) / (n[i] - r[i] + 0.5));
		}
		return sim;
	}
}
class Document {
	
	private Set<String> terms; 
	private String content;    
	
	public Document(String content) {
		terms = new HashSet<String>();
		this.content = content;
		String[] words = content.split("[^a-z^A-Z]");
		for (String word : words) {
			if (word.equals("")) continue;
			terms.add(word);
		}
	}
	
	public Set<String> getTerms() {
		return terms;
	}
	
	public boolean containsTerm(String term) {
		return terms.contains(term);
	}
	
	public String toString() {
		return content;
	}
}
