# Yield Curve
## Efficiency Mechanisms

We construct the yield curve from a given list of dates in linear time and in linear space due to having to store the data, where the date is the key and the values is a tuple of the bid and ask. The `getRate` method interpolates a given data point in constant time; $O(1)$. The bottleneck is with inserting the data points which can not be improved from $O(n)$, therefore the best runtime the program can achieve is $O(n)$, where n is the number of data points. 

To achieve this runtime it's necessary to have the constraints below. Justification for these requirements are given in the solution approach section.
* The dates must have a fixed frequency, i.e difference in time between successive points is constant.
* The first date and second date must be inserted in ascending order and in succession.

## Solution approach

We use a dictionary (HashMap in java) to insert and access data which can be done in contanst time. The disadvantage with a HashMap is that the data is stored in an unordred and unpredictable fashion. This is particularly problematic for cases where there is a need to get a key $k_h$ that is before or $k_l$ that is after a non key ($k$) value a key from the HashMap. Without a fixed frequency this would a require a linear scan of the map $`(k_l < k < k_h) = O(n)`$ making `getRate` linear. We are able to get $k_l$ and $k_h$ in constant time for fixed frequency data because we know that both all keys are a factor of the frequency therefore if we want to find the dates a date is in between we can simply round up and down from that date to the nearest factor of the frequency to get both $k_h$ and $k_l$ respectively.

The frequency is determined from the first and second entries thereafter the data points can be inserted anyhow.

We throw a `YieldCurveException` whenever an attempty is made to interpolate a date before the start date and linear interpolates for dates after the last date in the yield curve.

For more relaxed contrains such as no fixed frequency and random insersions of data one can use a Red-Black tree (TreeMap in java) to find $k_l$ and $k_h$ in $`O(logn)`$ making `getRate` $`O(logn)`$. Using a Hashmap in this scenario would make `getRate` $`O(n)`$ since this would require iterating through the data strucutre to find these keys. The solution making use of the TreeMap can be found in the _discuss_ branch.

## Data Structures

We use a `HashMap` to store and access data using the number of days from the start date, converted from the date. To store and access data from a HashMap is constant. We use a `List` to create a tuple containing the bid and ask values.
