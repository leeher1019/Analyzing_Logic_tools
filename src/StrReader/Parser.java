package StrReader;
public class Parser{
	public Integer[] parse(Integer sAcceleration, Integer sCurrent){
		int hAcceleration = 0;
		int hCurrent = 0;
		Integer[] v = new Integer[2];
		if(sAcceleration == 0 && sCurrent == 1){
			hAcceleration = 0;
			hCurrent = 0;
		}
		else if(sAcceleration == 1 && sCurrent == 1){
			hAcceleration = 1;
			hCurrent = 1;
		}
		v[0] = hAcceleration;
		v[1] = hCurrent;
		return v;
	}
}
