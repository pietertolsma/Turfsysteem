package com.tolsma.pieter.turf.gui.panel.stats;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;

import com.tolsma.pieter.turf.database.ItemManager;
import com.tolsma.pieter.turf.database.PersonManager;
import com.tolsma.pieter.turf.items.Item;
import com.tolsma.pieter.turf.items.Person;
import com.tolsma.pieter.turf.items.Item.Category;
import com.tolsma.pieter.turf.util.Constants;

public class StatisticsPanel extends JPanel{
	
	public static enum Statistics {
		DAILY, MONTHLY, YEARLY
	}

	private GridBagConstraints c;
	
	private Date startDate, endDate;
	
	private JLabel title;
	private Font nameFont;
	
	private JPanel buttonContainer;
	private JPanel container;
	private JScrollPane scrollPane;

	private JButton dailyButton, monthlyButton, yearlyButton;
	private JComboBox categorySelector;
	private String[] categories = {"Bier", "Speciaalbier", "Fris", "Eten"};
	private Category currentCategory = Category.PILS;
	
	private Statistics mode = Statistics.MONTHLY;
	
	public StatisticsPanel() {
		
		this.setLayout(new BorderLayout());
		
		buttonContainer = new JPanel();
		buttonContainer.setLayout(new GridLayout(1, 4));
		dailyButton = new JButton("Dag");
		monthlyButton = new JButton("Maand");
		yearlyButton = new JButton("Jaar");
		categorySelector = new JComboBox(categories);

		Font font = new Font("Arial", Font.BOLD, 40);
		
		dailyButton.setOpaque(true);
		monthlyButton.setOpaque(true);
		yearlyButton.setOpaque(true);

		dailyButton.setForeground(Color.WHITE);
        monthlyButton.setForeground(Color.WHITE);
        yearlyButton.setForeground(Color.WHITE);

        dailyButton.setFont(font);
        monthlyButton.setFont(font);
        yearlyButton.setFont(font);
        categorySelector.setFont(font);

		dailyButton.setBorderPainted(false);
		monthlyButton.setBorderPainted(false);
		yearlyButton.setBorderPainted(false);
		
		dailyButton.setBackground(Constants.TURQUOISE);
		monthlyButton.setBackground(Constants.TURQUOISE_HIGHLIGHT);
		yearlyButton.setBackground(Constants.TURQUOISE);
		
		dailyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mode = Statistics.DAILY;
				update();
			}
		});
		monthlyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mode = Statistics.MONTHLY;
				update();
			}
		});
		yearlyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mode = Statistics.YEARLY;
				update();
			}
		});
		categorySelector.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (categorySelector.getSelectedIndex()) {
					case 0:
						currentCategory = Category.PILS;
						break;
					case 1:
						currentCategory = Category.SPECIAALBIER;
						break;
					case 2:
						currentCategory = Category.FRIS;
						break;
					case 3:
						currentCategory = Category.ETEN;
						break;
					default:
						currentCategory = Category.PILS;
						break;
				}
				update();
			}
		});
		
		buttonContainer.add(dailyButton);
		buttonContainer.add(monthlyButton);
		buttonContainer.add(yearlyButton);
		buttonContainer.add(categorySelector);
		
		endDate = new Date();
		
		title = new JLabel("De Watex Klemcompetitie");
		Font bold = new Font("Arial", Font.BOLD, 50);
		nameFont = new Font("Arial", Font.PLAIN, 30);
		title.setFont(bold);


		container = new JPanel();
		container.setLayout(new GridBagLayout());
		scrollPane = new JScrollPane(container);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().putClientProperty("EnableWindowBlit", Boolean.TRUE);
        scrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);

		update();
	}
	
	public void update() {
		this.removeAll();
		add(title, BorderLayout.NORTH);
		
		dailyButton.setBackground(Constants.TURQUOISE);
		monthlyButton.setBackground(Constants.TURQUOISE);
		yearlyButton.setBackground(Constants.TURQUOISE);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		//Set the end time to 8 AM in the morning
        //Check whether the day should be incremented as well
		if (cal.get(Calendar.HOUR_OF_DAY) > 7) {
		    cal.add(Calendar.DATE, 1);
        }
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);
        endDate = cal.getTime();
		
		switch (mode) {
		case DAILY:
			dailyButton.setBackground(Constants.TURQUOISE_HIGHLIGHT);
			cal.add(Calendar.DATE, -1);
			startDate = cal.getTime();
			break;
		case MONTHLY:
		    cal.set(Calendar.DAY_OF_MONTH, 1);
			startDate = cal.getTime();
			monthlyButton.setBackground(Constants.TURQUOISE_HIGHLIGHT);
			break;
		case YEARLY:
			cal.set(Calendar.DAY_OF_YEAR, 1);
			startDate = cal.getTime();
			yearlyButton.setBackground(Constants.TURQUOISE_HIGHLIGHT);
			break;
		}
		
		add(buttonContainer, BorderLayout.NORTH);

		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridx = 0;
		c2.gridy = 0;
		container.removeAll();
		
		HashMap<Person, Integer> map = PersonManager.getInstance().getOrderedConsumption(startDate, endDate, currentCategory);
		Iterator<Map.Entry<Person, Integer>> it = map.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Person, Integer> res = it.next();
			Person p = res.getKey();
			JLabel nameLabel = new JLabel(p.getName());
			nameLabel.setFont(nameFont);
			c2.gridx = 0;
			container.add(nameLabel, c2);
			
			int amount = res.getValue();
			JLabel amountLabel = new JLabel(amount + " " + currentCategory);
			amountLabel.setFont(nameFont);
			c2.gridx++;
			container.add(amountLabel, c2);
			
			c2.gridy++;
		}
        scrollPane.revalidate();
		container.revalidate();
        container.repaint();
		scrollPane.repaint();
		add(scrollPane, BorderLayout.CENTER);

		this.repaint();
		this.revalidate();
	}
	
}
