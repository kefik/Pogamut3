package cz.cuni.amis.utils;

public class Tuple3<FIRST, SECOND, THIRD> {
	
	private FIRST first;
	private SECOND second;
	private THIRD third;

	public Tuple3(FIRST first, SECOND second, THIRD third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}

	public FIRST getFirst() {
		return first;
	}

	public void setFirst(FIRST first) {
		this.first = first;
	}

	public SECOND getSecond() {
		return second;
	}

	public void setSecond(SECOND second) {
		this.second = second;
	}

	public THIRD getThird() {
		return third;
	}

	public void setThird(THIRD third) {
		this.third = third;
	}

}
