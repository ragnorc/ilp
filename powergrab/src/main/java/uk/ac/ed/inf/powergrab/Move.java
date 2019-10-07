package uk.ac.ed.inf.powergrab;




class Move {
	
	final Direction direction;
	final Double coinGain;
	final Double powerGain;
	
	Move(Direction direction, Double coinGain, Double powerGain) {
		this.direction = direction;
		this.coinGain = coinGain;
		this.powerGain = powerGain;
		
		
	}
	
}