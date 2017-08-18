package pl.idedyk.japanese.dictionary.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pl.idedyk.japanese.dictionary.api.dto.KanjivgEntry;
import pl.idedyk.japanese.dictionary.tools.KanjivgReader;

public class TestParseSvgPath {

	public static void main(String[] args) throws Exception {
		
		Map<String, KanjivgEntry> result = KanjivgReader.readKanjivgSingleXmlFile(new File("/tmp/a/kanjivg.xml"));
		
		for (KanjivgEntry kanjivgEntry : result.values()) {
			
			List<String> strokePaths = kanjivgEntry.getStrokePaths();
			
			for (String strokePath : strokePaths) {
				
				System.out.println(strokePath);
				
				//
				
				StrokePath strokePathPath = parsePath(strokePath);
				
				PointF startPoint = strokePathPath.getStartPoint();
				
				List<Curve> curveList = strokePathPath.getCurveList();
				
				System.out.println("Start point: " + startPoint);
				
				for (Curve curve : curveList) {
					System.out.println("Curve: " + curve);
				}
			}
		}
		
		/*
		//StrokePath strokePath = parsePath("M54.89,15.5c-10.26,0-27.89,8.82-27.89,38.15c0,29.33,15.46,38.58,28.32,38.58c12.86,0,27.6-10.69,27.6-38.73c0.01-28.03-15.02-38-28.03-38");
		
		//StrokePath strokePath = parsePath("M54.5,88 c -0.83,0 -1.5,0.67 -1.5,1.5 0,0.83 0.67,1.5 1.5,1.5 0.83,0 1.5,-0.67 1.5,-1.5 0,-0.83 -0.67,-1.5 -1.5,-1.5");
		//StrokePath strokePath = parsePath("M54.5,88 c 0,0.83 0.67,1.5 1.5,1.5");
		//StrokePath strokePath = parsePath("M54.5,88 c 0,0.83,0.67,1.5,1.5,1.5");
		
		List<String> strokePaths = new ArrayList<String>();

		//strokePaths.add("M17.88,20.29c1.91,0.51,5.41,0.64,7.31,0.51c17.69-1.18,38.69-3.05,58.21-3.51c3.18-0.08,5.08,0.25,6.67,0.5");
		
		strokePaths.add("M17.88,20.29c1.91,0.51,5.41,0.64,7.31,0.51c17.69-1.18,38.69-3.05,58.21-3.51c3.18-0.08,5.08,0.25,6.67,0.5");
		strokePaths.add("M48.05,26.75c0.04,0.48,0.07,1.24-0.07,1.93c-0.85,4.08-6.22,14.11-11.57,17.82");
		strokePaths.add("M47.12,38.02c1.17,0.28,3.33,0.41,4.48,0.28c6.77-0.8,13.54-1.92,21.98-3.32c1.9-0.32,3.64-0.32,4.61-0.18");
		strokePaths.add("M32.39,56.74c2.47,0.48,5.17,0.4,7.67,0.17c10.06-0.92,26.6-2.98,38.22-3.8c2.76-0.19,5.72-0.24,8.33,0.07");
		strokePaths.add("M57.92,40.43c0.58,1.07,0.81,2.39,0.81,3.53C58.75,61.25,52.25,74,36.06,81.18");
		strokePaths.add("M61.03,60.17C70.29,63.76,83.25,74.25,87.25,81");
		strokePaths.add("M21.83,21.46c0.67,1.54,1,3.26,1,5.79c0,14.77-0.33,47-1.5,60.48c-0.26,3,1.01,3.97,2.81,3.8c16.11-1.53,44.49-2.91,60.2-2.56c3.32,0.07,6.12,0.24,8.17,0.71");
		
		/ *
		strokePaths.add("M 17.88,20.29 c 1.91,0.51 5.41,0.64 7.31,0.51 17.69,-1.18 38.69,-3.05 58.21,-3.51 3.18,-0.08 5.08,0.25 6.67,0.5");		
		strokePaths.add("M 48.05,26.75 c 0.04,0.48 0.07,1.24 -0.07,1.93 -0.85,4.08 -6.22,14.11 -11.57,17.82");
		strokePaths.add("M 47.12,38.02 c 1.17,0.28 3.33,0.41 4.48,0.28 6.77,-0.8 13.54,-1.92 21.98,-3.32 1.9,-0.32 3.64,-0.32 4.61,-0.18");
		strokePaths.add("M 32.39,56.74 c 2.47,0.48 5.17,0.4 7.67,0.17 10.06,-0.92 26.6,-2.98 38.22,-3.8 2.76,-0.19 5.72,-0.24 8.33,0.07");
		strokePaths.add("M 57.92,40.43 c 0.58,1.07 0.81,2.39 0.81,3.53 C 58.75,61.25 52.25,74 36.06,81.18");
		strokePaths.add("M 61.03,60.17 C 66.83499,67.883722 79.23773,77.927914 87.25,81");
		strokePaths.add("M 21.83,21.46 c 0.67,1.54 1,3.26 1,5.79 0,14.77 -0.33,47 -1.5,60.48 -0.26,3 1.01,3.97 2.81,3.8 16.11,-1.53 44.49,-2.91 60.2,-2.56 3.32,0.07 6.12,0.24 8.17,0.71");
		* /
		
		// test
		//strokePaths.add("M 17.88,20.29 c 1.91,0.51 5.41,0.64 7.31,0.51");
		//strokePaths.add("M17.88,20.29 c 1.91,0.51,5.41,0.64,7.31,0.51");
		
		for (String currentStrokePathString : strokePaths) {
			
			StrokePath strokePath = parsePath(currentStrokePathString);
			
			PointF startPoint = strokePath.getStartPoint();
			
			List<Curve> curveList = strokePath.getCurveList();
			
			System.out.println("Start point: " + startPoint);
			
			for (Curve curve : curveList) {
				System.out.println("Curve: " + curve);
			}
		}
		*/
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
            
            BEFORE_IF:
            if (c == ',' || c == '-' || c == ' ' || c == 'c' || c == 'C' || c == 's'
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
                    break BEFORE_IF;
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
                	isInMoveTo = false;
                	
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

		@SuppressWarnings("unused")
		public float getX() {
			return x;
		}

		@SuppressWarnings("unused")
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

		@SuppressWarnings("unused")
		public PointF getP1() {
			return p1;
		}

		@SuppressWarnings("unused")
		public PointF getP2() {
			return p2;
		}

		@SuppressWarnings("unused")
		public PointF getP3() {
			return p3;
		}

		@SuppressWarnings("unused")
		public boolean isRelative() {
			return relative;
		}

		@SuppressWarnings("unused")
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
