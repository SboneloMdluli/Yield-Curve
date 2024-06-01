We use a dictionary (HashMap in java) to insert and access data which can be done in contanst time. The disadvantage with a HashMap is that the data is stored in an unordred and unpredictable fashion. This is particularly problematic for cases where there is a need to get a non key value ($k$) that is before $k_h$ or after $k_l$. Without having a fixed frequency this would a require linear scan of the map $`k_l \leq k \leq k_h = O(n)`$ making getRate linear instead of constant time. We are able to get $k_l$ and $k_h$ in constant time for fixed frequency data because we know that both all keys are a factor of the frequency therefore if we want to find the dates a date is inbetween we can simply round up and down from that date to the nearest factor of the frequency to get both $k_h$ and $k_l$ respectively.

The frequency is determined from the first and second entries thereafter the data points can be inserted anyhow.

For more relaxed contrains such as no fixed frequency and random insersion of data one can use a Red-Black tree (TreeMap in java) to find $k_l$ and $k_h$ in $`O(logn)`$ making getRate $`O(logn)`$. Using a Hashmap in this scenario would make getRate $`O(nlogn)`$ since this will require some form of sorting. The solution making use of the TreeMap can be found in the discuss branch.

## Data Structures

We use a HashMap to store and access data using the number of days from the start date, covreted from the date. To store and access data from a HashMap is constant. We use a list a create a tuple for the bid and ask values.