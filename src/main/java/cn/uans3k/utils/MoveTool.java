package cn.uans3k.utils;

public class MoveTool {
	public static float getBound(float input, float increase, float left, float right) {
		float temp = input + increase;
		temp = temp < left ? left : temp;
		temp = temp > right ? right : temp;
		return temp;
	}
	
	public static float getBound(float input,float left, float right) {
		return getBound(input, 0, left, right);
	}
	
	public static int getBound(int input,int increase,int left,int right) {
		int temp = input + increase;
		temp = temp < left ? left : temp;
		temp = temp > right ? right : temp;
		return temp;
	}
	public static int getBound(int input,int left,int right) {
		return getBound(input, 0, left, right);
	}
	public static int[] getBoundAndOverDelta(int input,int increase,int left,int right){
		int temp[]=new int[2];
		 temp[0]=getBound(input, increase,left, right);
		 temp[1]=input+increase-temp[0];
		 return temp;
	}
	public static int[] getBoundAndOverDelta(int input,int left,int right){
		return getBoundAndOverDelta(input,0,left, right);
	}
	
	public static int getLoopBound(int input,int increase,int left,int right){
		int temp= input+increase;
		int length=right-left+1;
		temp=temp>right?left+temp%length-1:temp;
		temp=temp<left?right-(right-temp)%length:temp;
		return temp;
	}
}
