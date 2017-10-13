package cz.cuni.amis.utils.statistic;

public class MovingAverage<TYPE> implements IMovingAverage<TYPE> {

	private IAveragator<TYPE> averagator;

	public MovingAverage(IAveragator<TYPE> averagator) {
		this.averagator = averagator;
	}
	
	@Override
	public void add(TYPE item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TYPE getAverage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCurrentLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEnoughValues() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMaxLength(int length) {
		// TODO Auto-generated method stub
		
	}

}
