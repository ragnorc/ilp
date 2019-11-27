package uk.ac.ed.inf.powergrab;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;


/******************************************************************************
 *  Class:       WeightedRandomCollection
 *  Author:  	 s1614102 
 *  Description: This class represents the data structure used for picking items
 *  			 with a probability proportional to the given weight. In the context
 *  			 of the drone, the weight is the utility of the given station.
 *  			 It uses a Treemap to store the cumulative sums of the stations' 
 *    			 utilities and  picks an element by generating a number uniformly 
 *    			 at random between 0 and the total sum and returns the element with the least 
 *    			 cumulative weight greater than that number as to emulate a bucket in which 
 *    			the elements take up space according to their weight. The probability 
 *    			is then proportional to the space.
 *  
 *
 ******************************************************************************/

public class WeightedRandomCollection<E> {
    private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
    private final Random random;
    private double total = 0;

    public WeightedRandomCollection() {
        this(new Random());
    }

    public WeightedRandomCollection(Random random) {
        this.random = random;
    }

    public WeightedRandomCollection<E> add(double weight, E result) {
    	
        if (weight <= 0) return this;
        total += weight;
        map.put(total, result);
        return this;
    }
    
    
    /**
	 * Method: next
	 * Description: Picks an element according to the probability distribution described above.
	 *
	 */
    
    public E next() {
    	if (map.size() <=2) {
    		return map.firstEntry().getValue();
    	}
    	else {
    		double value = random.nextDouble() * total;
            return map.higherEntry(value).getValue();
    	}
        
    }
    
    public int size() {
    	return map.size();
    }
}