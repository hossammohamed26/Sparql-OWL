package jenaFirst;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.VCARD;
import org.apache.jena.vocabulary.XSD;

public class main {

	public static void main(String[] args) throws ParseException, IOException {
		String line = "";  
		String splitBy = ",";  
		 
		//parsing a CSV file into BufferedReader class constructor  
		BufferedReader br = new BufferedReader(new FileReader("england-premier-league-players-2018-to-2019-stats.csv")); 
		BufferedReader br2 = new BufferedReader(new FileReader("england-premier-league-teams-2018-to-2019-stats.csv")); 
		BufferedReader br3 = new BufferedReader(new FileReader("england-premier-league-matches-2018-to-2019-stats.csv")); 
		int count=0;
		ArrayList<ArrayList<String>> outerplayers = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> outerteams = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> outermatches = new ArrayList<ArrayList<String>>();
	    ArrayList<String> inner = new ArrayList<String>();   
	    ArrayList<String> propertiesplayers = new ArrayList<String>(); 
	    ArrayList<String> propertiesteams = new ArrayList<String>(); 
	    ArrayList<String> propertiesmatches = new ArrayList<String>(); 
		while ((line = br.readLine()) != null)   //returns a Boolean value  
		{  
			if (count!=0)
			{
				
				String[] players = line.split(splitBy);    // use comma as separator  
				for (int i=0;i<19;i++)
				{
					
					
					inner.add(players[i]);
				}
				
				outerplayers.add(inner);
				
				inner = new ArrayList<String>(); 
			}
			else {
				String[] players = line.split(splitBy);    // use comma as separator  
				for (int i=0;i<19;i++)
				{
					if (i==5)
					{
						propertiesplayers.add("Current_Club");
					}
						
					else {
						propertiesplayers.add(players[i]);
					}
				}
				
			}
			
			count++;
			
		}  
		inner = new ArrayList<String>(); 
		count=0;
		while ((line = br2.readLine()) != null)   //returns a Boolean value  
		{  
			if (count!=0)
			{
				
				String[] players = line.split(splitBy);    // use comma as separator  
				for (int i=0;i<29;i++)
				{
					inner.add(players[i]);
				}
				
				outerteams.add(inner);
				
				inner = new ArrayList<String>(); 
			}
			else {
				String[] players = line.split(splitBy);    // use comma as separator  
				for (int i=0;i<29;i++)
				{
					
					propertiesteams.add(players[i]);
				}
				
			}
			
			count++;
			
		}
		count=0;
		inner = new ArrayList<String>(); 
		while ((line = br3.readLine()) != null)   //returns a Boolean value  
		{  
			if (count!=0)
			{
				
				String[] players = line.split(splitBy);    // use comma as separator  
				for (int i=0;i<18;i++)
				{
					inner.add(players[i]);
				}
				
				outermatches.add(inner);
				
				inner = new ArrayList<String>(); 
			}
			else {
				String[] players = line.split(splitBy);    // use comma as separator  
				for (int i=0;i<18;i++)
				{
					
					if (i==7)
					{
						propertiesmatches.add("Game_week");
					}
					else if (i==8)
					{
						
						propertiesmatches.add("Pre-Match_PPG_Home");
					}
					else if (i==9)
					{
						
						propertiesmatches.add("Pre-Match_PPG_Away");
					}
					else {
						propertiesmatches.add(players[i]);
					}
				}
				
			}
			
			count++;
			
		}
		
		
		// some definitions
		String baseUri = "http://www.semanticweb.org/football/ontologies#";
		// create an empty Model
		OntModel  model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM );
		OntClass playersClass = model.createClass( baseUri + "players" );
		OntClass teamsClass = model.createClass( baseUri + "teams" );
		OntClass matchesClass = model.createClass( baseUri + "matches" );
		
		ObjectProperty hasTeam = model.createObjectProperty( baseUri + "hasTeam" );
		ObjectProperty hasAwayTeam = model.createObjectProperty( baseUri + "hasAwayTeam" );
		ObjectProperty hasHomeTeam = model.createObjectProperty( baseUri + "hasHomeTeam" );
		ArrayList<Individual> indviduals = new ArrayList<Individual>(); 		
		
		for (int i=0;i<outerteams.size();i++)
		{
			String name=outerplayers.get(i).get(1);
			if (outerteams.get(i).get(0).contains(" "))
			{
				name=outerteams.get(i).get(1).replace(" ", "_");
			}
			Individual x = teamsClass.createIndividual(baseUri+name);
			indviduals.add(x);
			for (int j=0;j<29;j++)
			{
				
				DatatypeProperty y = model.createDatatypeProperty( baseUri + propertiesteams.get(j) );
				y.addDomain( teamsClass );
				if (j>3 && j!=14 && j!=15 && j!=16) {
					y.addRange( XSD.xint );
				}
				else if (j<3){
					y.addRange( XSD.xstring );
				}
				else {
					y.addRange( XSD.xfloat );
				}
				
				x.addProperty(y, outerteams.get(i).get(j));
			}
			
		}
		
		for (int i=0;i<outerplayers.size();i++)
		{
			String name=outerplayers.get(i).get(0);
			if (outerplayers.get(i).get(0).contains(" "))
			{
				name=outerplayers.get(i).get(0).replace(" ", "_");
			}
			Individual x = playersClass.createIndividual(baseUri+name);
			
			for (int j=0;j<indviduals.size();j++)
			{
				Individual objx=indviduals.get(j);
				if(outerplayers.get(i).get(5).equals(objx.getURI().substring(47)))
				{
					//System.out.println(outerplayers.get(i).get(5));
					//System.out.println(objx.getURI().substring(47));
					x.addProperty(hasTeam, objx);
				}
				
				
			}
			System.out.println("-------------------");
			for (int j=0;j<19;j++)
			{
				
				DatatypeProperty y = model.createDatatypeProperty( baseUri + propertiesplayers.get(j) );
				y.addDomain( playersClass );
				if (j==1 || j==6 || j==7 || j==8 || j>9) {
					y.addRange( XSD.xint );
				}
				else {
					y.addRange( XSD.xstring );
				}
				x.addProperty(y, outerplayers.get(i).get(j));
			}
			
		}
		
		
		for (int i=0;i<outermatches.size();i++)
		{
			Individual x = matchesClass.createIndividual(baseUri+i);
			
			
			for (int j=0;j<indviduals.size();j++)
			{
				Individual objx=indviduals.get(j);
				if(outermatches.get(i).get(4).equals(objx.getURI().substring(47)))
				{
					System.out.println(outermatches.get(i).get(4));
					System.out.println(objx.getURI().substring(47));
					x.addProperty(hasHomeTeam, objx);
				}
				System.out.println("-------------------");
				if (outermatches.get(i).get(5).equals(objx.getURI().substring(47)))
				{
					System.out.println(outermatches.get(i).get(5));
					System.out.println(objx.getURI().substring(47));
					x.addProperty(hasAwayTeam, objx);
				}
				System.out.println("-------------------");
				
			}
			//x.addProperty(hasHomeTeam, objx);
			//x.addProperty(hasAwayTeam, objxx);
			for (int j=0;j<18;j++)
			{
				
				DatatypeProperty y = model.createDatatypeProperty( baseUri + propertiesmatches.get(j) );
				y.addDomain( matchesClass );
				
				if (j<3 || j==4 || j==5 || j==6) {
					y.addRange( XSD.xstring );
				}
				else if (j==10 || j==11){
					y.addRange( XSD.xfloat );
				}
				else {
					y.addRange( XSD.xint );
				}
				
				x.addProperty(y, outermatches.get(i).get(j));
				
			}
			
		}
		
		
		PrintStream out = new PrintStream(new FileOutputStream("test.owl"));
		System.setOut(out);
		
		model.write(System.out);
		
}
}