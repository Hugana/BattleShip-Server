package db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormatSymbols;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DummyDB {
	private List<String> lista=null;
	public DummyDB(String items) {
		if(items.compareToIgnoreCase("paises")==0)
			lista=CountriesDB("");  // pt, en, fr, it
		if(items.compareToIgnoreCase("meses")==0)
			lista=MonthsDB("it");
		if(items.compareToIgnoreCase("palavras")==0)
			lista=WordsDB();
		if(items.compareToIgnoreCase("arterias")==0)
			lista=ArteriasDB();
	}
	
	private ArrayList<String> CountriesDB(String language) {
		/* A list containing all the country/regions names in the world:*/
		ArrayList<String> countries = new ArrayList<String>();
        // Map ISO countries to custom country object
        String[] countryCodes = Locale.getISOCountries();
        for (String countryCode : countryCodes){
            Locale locale = new Locale(language, countryCode);
        	countries.add(locale.getDisplayCountry());
        }
		Collections.sort(countries);
		return countries;
	}
	
	private ArrayList<String> MonthsDB(String language) {
		ArrayList<String> months = new ArrayList<String>(
				Arrays.asList(new DateFormatSymbols(new Locale(language)).getMonths())); 
		Collections.sort(months);
		return months;
	}
	
	private ArrayList<String> WordsDB() {
		ArrayList<String> words = new ArrayList<String>(
		Arrays.asList("Gazeta", "publica", "hoje","no","jornal","uma","breve","nota","de","faxina","na","quermesse"));
		Collections.sort(words);
		return words;
	}
	
	private ArrayList<String> ArteriasDB() {
		ArrayList<String> arterias = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
		        DummyDB.class.getResourceAsStream("arterias.txt")));
		String line = null;
		try {
			while((line=reader.readLine())!=null)
				arterias.add(line);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return arterias;
	}
	
	/**
	 * @param query - String que é comparado com o inicio
	 * @param nLetters - Quantidade de letras minimo exigido para preencher a lista
	 * @param nItems - Quantidade de sugestões devolvidas
	 * @return - Lista de items que satisfazem a query
	 */
	public List<String> getData(String query, int nLetters, int nItems) {
		List<String> matched = new ArrayList<String>();
		if(query==null || query.length()<nLetters) // numero minimo de letras
			return matched;
		query = query.toLowerCase();
		for(int i=0; i<lista.size(); i++) {
			String item = lista.get(i).toLowerCase();
			if( item.toLowerCase().startsWith(query)) { 
				matched.add(lista.get(i));
			if( matched.size()>nItems)  // limita o numero de respostas
				break;
			}
		}
		return matched;
	}
}