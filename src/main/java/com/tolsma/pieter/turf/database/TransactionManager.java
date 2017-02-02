package com.tolsma.pieter.turf.database;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.tolsma.pieter.turf.Application;
import com.tolsma.pieter.turf.items.Item;
import com.tolsma.pieter.turf.items.Item.Category;
import com.tolsma.pieter.turf.items.Person;
import com.tolsma.pieter.turf.items.Transaction;

public class TransactionManager {

	private static TransactionManager instance = new TransactionManager();

	private String file_dir = Application.DATA_DIR + "/transactions/";

	private ArrayList<Transaction> transactions;

	public TransactionManager() {
		File dir = new File(file_dir);
		if (!dir.exists()) {
			dir.mkdir();
		}
	}

	public void init() {
		transactions = readFromFile(new Date());
	}

	public static TransactionManager getInstance() {
		return instance;
	}

	public ArrayList<Transaction> getTransactions() {
		return transactions;
	}
	
	public ArrayList<Transaction> getTransactionsAt(Date date) {
		ArrayList<Transaction> result = readFromFile(date);
		
		Iterator<Transaction> it = result.iterator();
		while(it.hasNext()) {
			Transaction trans = it.next();
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
			if (!fmt.format(trans.getDate()).equals(fmt.format(date))) {
				it.remove();
			}
		}
		
		return result;
	}
	
	public void removeTransaction(UUID transactionId) {
		Iterator<Transaction> it = transactions.iterator();
		while(it.hasNext()) {
			Transaction t = it.next();
			if (t.getId().equals(transactionId)) {
				ItemManager.getInstance().getItem(t.getItem().getId()).subtractStock(-t.getCount());
				it.remove();
				save();
				return;
			}
		}
	}

	public void save(ArrayList<Transaction> newList) {
		JSONObject main = new JSONObject();
		JSONArray transactionsArray = new JSONArray();

		for (Transaction t : newList) {
			transactionsArray.add(t.getTransactionJSON());
		}

		main.put("transactions", transactionsArray);

		try (FileWriter writer = new FileWriter(new File(getFilePath(new Date())))) {
			writer.write(main.toJSONString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void save() {
		save(transactions);
	}

	public void addTransaction(Transaction trans) {
		transactions.add(0, trans);
		save();
	}
	
	public String getFilePath(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return file_dir + cal.get(Calendar.WEEK_OF_YEAR) + "_" + cal.get(Calendar.YEAR) + ".json";
	}
	
	public ArrayList<Transaction> getSpecificTransactions(Date date, Person person, Category itemCategory) {
		ArrayList<Transaction> results = readFromFile(date);
		
		Iterator<Transaction> it = results.iterator();
		while(it.hasNext()) {
			Transaction trans = it.next();
			if (!trans.getItem().getCategory().equals(itemCategory) || !trans.getParticipants().contains(person)) {
				it.remove();
			}
		}
		return results;
	}

	private ArrayList<Transaction> readFromFile(Date date) {
		ArrayList<Transaction> results = new ArrayList<>();
		JSONParser parser = new JSONParser();
		String filePath = getFilePath(date);

		File file = new File(filePath);
		if (!file.exists()) {
			try {
				JSONObject obj = new JSONObject();
				obj.put("transactions", new JSONArray());
				FileWriter writer = new FileWriter(file);
				writer.write(obj.toJSONString());
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try (FileReader reader = new FileReader(file)) {
			Object obj = parser.parse(reader);
			JSONObject jsonObject = (JSONObject) obj;

			JSONArray names = (JSONArray) jsonObject.get("transactions");
			if (names.isEmpty())
				return results;
			Iterator<JSONObject> it = names.iterator();
			while (it.hasNext()) {
				JSONObject obj1 = it.next();

				Item item = ItemManager.getInstance().getItem(UUID.fromString((String) obj1.get("item_id")));
				ArrayList<Person> participants = new ArrayList<>();
				JSONArray personArray = (JSONArray) obj1.get("participants");
				Iterator it2 = personArray.iterator();
				while (it2.hasNext()) {
					UUID personId = UUID.fromString((String) it2.next());
					Person person = PersonManager.getInstance().getPerson(personId);
					participants.add(person);
				}

				if (item != null) {
					Date date1 = new Date(Long.valueOf((String) obj1.get("created_at")));
					results.add(new Transaction(item, Integer.valueOf((String) obj1.get("total_amount")), participants, UUID.fromString((String) obj1.get("transaction_id")), date1));
				}
			}

		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return results;
	}
}