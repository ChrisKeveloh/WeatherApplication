package assests;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.json.simple.JSONObject;
public class WeatherAppGUI extends JFrame{

	private JSONObject weatherData;
	
	
	public WeatherAppGUI()
	{
		super("Weather app");
		
	
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setSize(450,650);
		
		setLocationRelativeTo(null); 
		
		
		
		setLayout(null);
		
		
		setResizable(false);
		
		addGuiComponents();
		
		
	}

	private void addGuiComponents() {
		
		JTextField searchTextField = new JTextField();
		
		
		searchTextField.setBounds(15,15,351,45);
		
		searchTextField.setFont(new Font("Diaglog", Font.PLAIN, 24));
		
		
		add(searchTextField);
		
		

		
		JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
		weatherConditionImage.setBounds(0,100,450,217);
		add(weatherConditionImage);
		
		JLabel temperatureText = new JLabel("70 F");
		temperatureText.setBounds(0,280,450,217);
		temperatureText.setFont(new Font("Dialog",Font.BOLD, 48));

		temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
		add(temperatureText);

		JLabel weatherConditionDesc = new JLabel("Cloudy");
		weatherConditionDesc.setBounds(0, 325, 450, 36);
		weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
		weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
		add(weatherConditionDesc);


		JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
		humidityImage.setBounds(15,500,74,66);
		add(humidityImage);

		JLabel humidityText = new JLabel("<html><b>Humidity</b> 100% </html>");
		humidityText.setBounds(90, 500, 85, 55);
		humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
		add(humidityText);

		JLabel windspeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
		windspeedImage.setBounds(240,500,74,66);
		add(windspeedImage);

		JLabel windspeedDesc = new JLabel("<html><b>Windspeed</b> 4.1MPH </html>");
		windspeedDesc.setBounds(330,500,85,55);
		windspeedDesc.setFont(new Font("Dialog", Font.PLAIN, 16));
		add(windspeedDesc);

		// search button
		JButton searchButton = new JButton(loadImage("src/assets/search.png"));
		
		// change the cursor to hand cursor when hovering over this button
		searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		searchButton.setBounds(375, 13,47,45);
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				//get location from user
				String userInput = searchTextField.getText();

				//valid input - remove whitespace to ensure non-emtpy text

				if(userInput.replaceAll("\\s","").length() <= 0)
				{
					return;
				}

				//retrieve weather data

				weatherData = WeatherApp.getWeatherData(userInput);

				//update gui


				//update weather image
				String weatherCondition = (String) weatherData.get("weather_condition");

				// depending on the condition we will update the weather image that corresponds with the condition

				switch(weatherCondition)
				{
					case "Clear": 
						weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
						break;
					case "Cloudy": 
						weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
						break;
					case "Rain": 
						weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
						break;
					case "Snow": 
						weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));

				}

				//update temperature text

				double temperature = (double) weatherData.get("temperature");
				temperatureText.setText(temperature + " F");

				//update weather condition text

				weatherConditionDesc.setText(weatherCondition);

				// update himidity text

				long humidity = (long) weatherData.get("humidity");
				humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

				// update windspeed text
				double windspeed = (double) weatherData.get("windspeed");
				windspeedDesc.setText("<html><b>Windspeed</b> " + windspeed + "MPH%</html>");



			}

		});
		{

		}
		add(searchButton);


		
	}
	
	private ImageIcon loadImage(String resourcePath) {
	
	try
	{
		BufferedImage image = ImageIO.read(new File(resourcePath));
		
		
		return new ImageIcon(image);
	}catch(IOException e)
	{
		e.printStackTrace();
	}
	
	System.out.println("Could not find resourcepath");
	return null;

}
}