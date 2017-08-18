package pl.idedyk.japanese.dictionary.test;

import java.util.ArrayList;
import java.util.List;

public class TestParseSvgPath {

	public static void main(String[] args) {
		
		//StrokePath strokePath = parsePath("M54.89,15.5c-10.26,0-27.89,8.82-27.89,38.15c0,29.33,15.46,38.58,28.32,38.58c12.86,0,27.6-10.69,27.6-38.73c0.01-28.03-15.02-38-28.03-38");
		
		//StrokePath strokePath = parsePath("M54.5,88 c -0.83,0 -1.5,0.67 -1.5,1.5 0,0.83 0.67,1.5 1.5,1.5 0.83,0 1.5,-0.67 1.5,-1.5 0,-0.83 -0.67,-1.5 -1.5,-1.5");
		StrokePath strokePath = parsePath("M54.5,88 c 0,0.83 0.67,1.5 1.5,1.5");
		
		
		PointF startPoint = strokePath.getStartPoint();
		
		List<Curve> curveList = strokePath.getCurveList();
		
		System.out.println("Start point: " + startPoint);
		
		for (Curve curve : curveList) {
			System.out.println("Curve: " + curve);
		}
	}
	
    private static StrokePath parsePath(String path) {

        boolean isInMoveTo = false;

        StringBuffer buff = new StringBuffer();
        Float x = null;
        Float y = null;

        PointF p1 = null;
        PointF p2 = null;
        PointF p3 = null;

        StrokePath result = null;
        boolean relative = false;
        boolean smooth = false;

        for (int i = 0; i < path.length(); i++) {
            
        	char c = path.charAt(i);
            
            if (c == 'M' || c == 'm') {
                isInMoveTo = true;
                continue;
            }

            if (Character.isDigit(c) || c == '.') {
                buff.append(Character.toString(c));
            }

            if (c == ',' || c == '-' || c == 'c' || c == 'C' || c == 's'
                    || c == 'S' || i == (path.length() - 1)) {
            	
                String floatStr = buff.toString();
                
                // System.out.println("i: " + i);
                // System.out.println("c: " + c);
                // System.out.println("floastStr: " + floatStr);
                buff = new StringBuffer();
                
                if (c == '-') {
                    buff.append(c);
                }

                if ("".equals(floatStr)) {
                    continue;
                }

                try {
                    float f = Float.parseFloat(floatStr);
                    if (x == null) {
                        x = f;
                    } else {
                        y = f;
                    }
                } catch (NumberFormatException e) {
                	/*
                    ErrorReporter er = ErrorReporter.getInstance();
                    er.putCustomData("pathStr", path);
                    er.putCustomData("floatStr", floatStr);
                    er.handleSilentException(e);
                    */
                    throw e;
                }
            }

            if (x != null && y != null) {
            	            	            	
                PointF p = new PointF(x, y);
                x = null;
                y = null;

                if (isInMoveTo) {
                    result = new StrokePath(p);
                } else {
                    if (p1 == null) {
                        p1 = p;
                    } else if (p1 != null && p2 == null) {
                        p2 = p;
                    } else if (p1 != null && p2 != null && p3 == null) {
                        p3 = p;
                    }

                    if (!smooth) {
                        if (p1 != null && p2 != null && p3 != null) {
                            result.addCurve(new Curve(p1, p2, p3, relative,
                                    smooth));
                            p1 = null;
                            p2 = null;
                            p3 = null;
                        }
                    } else {
                        if (p1 != null && p2 != null) {                        	
                            result.addCurve(new Curve(null, p1, p2, relative,
                                    smooth));
                            p1 = null;
                            p2 = null;
                            p3 = null;
                        }
                    }
                }
            }

            if (c == 'c' || c == 'C' || c == 's' || c == 'S') {
                relative = (c == 'c' || c == 's');
                smooth = (c == 's' || c == 'S');
                isInMoveTo = false;
            }
        }

        return result;
    }
    
    private static class StrokePath {

    	private PointF startPoint;
    	
    	private List<Curve> curveList = new ArrayList<Curve>();
    	
    	
    	public StrokePath(PointF startPoint) {
    		this.startPoint = startPoint;
    	}
    	
		public void addCurve(Curve curve) {
			curveList.add(curve);			
		}

		public PointF getStartPoint() {
			return startPoint;
		}

		public List<Curve> getCurveList() {
			return curveList;
		}   
    }

	private static class PointF {
		
		private float x;
		private float y;
		
		public PointF(float x, float y) {
			this.x = x;
			this.y = y;
		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}

		@Override
		public String toString() {
			return "PointF [x=" + x + ", y=" + y + "]";
		}
	}
	
	private static class Curve {
		
		private PointF p1;
		private PointF p2;
		private PointF p3;
		
		private boolean relative;
		private boolean smooth;
		
		public Curve(PointF p1, PointF p2, PointF p3, boolean relative, boolean smooth) {
			
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
			
			this.relative = relative;
			this.smooth = smooth;
		}

		public PointF getP1() {
			return p1;
		}

		public PointF getP2() {
			return p2;
		}

		public PointF getP3() {
			return p3;
		}

		public boolean isRelative() {
			return relative;
		}

		public boolean isSmooth() {
			return smooth;
		}

		@Override
		public String toString() {
			return "Curve [p1=" + p1 + ", p2=" + p2 + ", p3=" + p3 + ", relative=" + relative + ", smooth=" + smooth
					+ "]";
		}
	}
}
