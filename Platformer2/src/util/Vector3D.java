package util;

public class Vector3D extends Point3D{

	public Vector3D(double x, double y, double z) {
		super(x, y, z);
	}
	
	public Vector3D(Point3D a, Point3D b) {
		super(b.x - a.x, b.y - a.y, b.z - a.z);
	}
	
	public Vector3D(Vector3D v) {
		super(v.x, v.y, v.z);
	}
	
	public Vector3D(Point3D p) {
		super(p.x, p.y, p.z);
	}
	
	public void addVector(Vector3D v) {
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
	}
	
	public void subtractVector(Vector3D v) {
		this.x -= v.x;
		this.y -= v.y;
		this.z -= v.z;
	}
	
	public double getMagnitude() {
		double xyDist = MathTools.dist(0, 0, x, y);
		return MathTools.dist(0, 0, xyDist, z);
	}
	
	public void multiply(double val) {
		this.x *= val;
		this.y *= val;
		this.z *= val;
	}
	
	public void normalize() {
		double mag = this.getMagnitude();
		this.x /= mag;
		this.y /= mag;
		this.z /= mag;
	}
	
	public void setMagnitude(double mag) {
		this.normalize();
		this.x *= mag;
		this.y *= mag;
		this.z *= mag;
	}
	
	public void rotate(double radX, double radY, double radZ) {
		rotateX(radX);
		rotateY(radY);
		rotateZ(radZ);
	}
	
	public void rotateX(double xRot) {
		double x = this.x;
		double y = this.y;
		double z = this.z;
		this.x = x;
		this.y = ((y * Math.cos(xRot)) + (z * -Math.sin(xRot)));
		this.z = ((y * Math.sin(xRot)) + (z * Math.cos(xRot)));
	}
	public void rotateY(double yRot) {
		double x = this.x;
		double y = this.y;
		double z = this.z;
		this.x = (x * Math.cos(yRot)) + (z * Math.sin(yRot));
		this.y = y;
		this.z = (x * -Math.sin(yRot)) + (z * Math.cos(yRot));
	}
	public void rotateZ(double zRot) {
		double x = this.x;
		double y = this.y;
		double z = this.z;
		this.x = (x * Math.cos(zRot)) + (y * -Math.sin(zRot));
		this.y = (x * Math.sin(zRot)) + (y * Math.cos(zRot));
		this.z = z;
	}
	
}
