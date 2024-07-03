public class BezierCurve {
  private float p0;
  private float p1;
  private float p2;
  private float p3;

  public BezierCurve(float p0, float p1, float p2, float p3) {
    this.p0 = p0;
    this.p1 = p1;
    this.p2 = p2;
    this.p3 = p3;
  }

  public float getCurve(float t) {
    float u = 1 - t;
    float tt = t * t;
    float uu = u * u;
    float uuu = uu * u;
    float ttt = tt * t;
    return (uuu * p0) + (3 * uu * t * p1) + (3 * u * tt * p2) + (ttt * p3);
  }
}
