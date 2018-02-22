package Objets;

public class PaireXY {
	public float x,y;

	public PaireXY(float x,float y)
	{
		this.x=x;this.y=y;
	}
	public PaireXY(PaireXY p)
	{
		this.x=p.x;this.y=p.y;
	}
	public void setXY(float x,float y)
	{
		this.x=x;this.y=y;
	}
	public void setXY(PaireXY p)
	{
		this.x=p.x;this.y=p.y;
	}
	public void addXY(float dx,float dy)
	{
		this.x+=dx;this.y+=dy;
	}
	public void addXY(PaireXY p)
	{
		this.x+=p.x;this.y+=p.y;
	}
}
