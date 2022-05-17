package math;

public class Vector2
{

    public double x, y;

    public Vector2(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector2 copy()
    {
        return new Vector2(this.x, this.y);
    }

    public boolean equals(Vector2 other)
    {
        return this.x == other.x && this.y == other.y;
    }

    public double getMag()
    {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public void setMag(double mag)
    {
        this.normalize();
        this.mult(mag);
    }
    
    public void normalize()
    {
        double mag = this.getMag();
        this.div(mag);
    }

    public Vector2 normalized()
    {
        Vector2 v = this.copy();
        v.normalize();
        return v;
    }

    // Arithmetic
    public void add(double n)
    {
        this.x += n;
        this.y += n;
    }

    public void sub(double n)
    {
        this.x -= n;
        this.y -= n;
    }

    public void mult(double n)
    {
        this.x *= n;
        this.y *= n;
    }

    public void div(double n)
    {
        this.x /= n;
        this.y /= n;
    }

    public void add(Vector2 other)
    {
        this.x += other.x;
        this.y += other.y;
    }

    public void mult(Vector2 other)
    {
        this.x *= other.x;
        this.y *= other.y;
    }

    public void div(Vector2 other)
    {
        this.x /= other.x;
        this.y /= other.y;
    }

    public void sub(Vector2 other)
    {
        this.x -= other.x;
        this.y -= other.y;
    }

    @Override
    public String toString()
    {
        return '('+this.x+", "+this.y+')';
    }
}
