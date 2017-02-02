package com.tolsma.pieter.turf.gui.panel.options;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.tolsma.pieter.turf.Application;
import com.tolsma.pieter.turf.gui.MainFrame;

public class MainOptionsPanel extends JPanel {

	private JButton editItems;
	private JButton editUsers;
	private JButton backButton;
	private MainFrame mainFrame;

	public MainOptionsPanel(MainFrame mainFrame) {
		this.setLayout(new GridLayout(1, 2));

		this.mainFrame = mainFrame;

		editItems = new JButton("Edit Inventory");
		editItems.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.showEditInventory();
			}
		});
		add(editItems);

		editUsers = new JButton("Edit Users");
		editUsers.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String choice = JOptionPane.showInputDialog(mainFrame, "Enter password (Level 2 clearance)", "Password", JOptionPane.PLAIN_MESSAGE);
				if (choice != null && choice.equals(Application.PASSWORD_LEVEL_2))
					mainFrame.showEditPersons();
			}
		});
		add(editUsers);

		backButton = new JButton("Back");
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.showMain();
			}
		});
		add(backButton);
	}

	public void update() {
		mainFrame.update();
	}

	public void showMain() {
		mainFrame.update();
		mainFrame.showOptions();
	}

}