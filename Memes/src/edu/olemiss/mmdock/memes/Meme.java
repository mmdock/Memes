package edu.olemiss.mmdock.memes;

public class Meme {

	public Meme(String name, String image) {
		super();
		this.name = name;
		this.image = image;
	}

	private String name;
	private String image;

	public String getName() {
		return name;
	}

	public void setName(String nameText) {
		name = nameText;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

}
