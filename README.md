# Travelling Thief Problem solver
Travelling Thief Problem solver created as part of the Metaheuristics in Problem Solving course
-
The Travelling Thief Problem (TTP) involves a thief who has to visit n cities (each city must be visited exactly once), where specific items are located, and in each cichy choose which items from this city they should put in their backpack. 
The thief's backpack has a limited capacity and the items have specific weights. The heavier the backpack, the slower the thief moves from city to city. The thief's goal is to obtain the most valuable items in the shortest amount of time.

The TTP is an NP-hard problem that combines two other NP-hard problems: the Travelling Salesman Problem (TSP) and the Knapsack Problem (KNP). 
In the TSP, a salesman visits n cities, each of which must be visited exactly once, and then returns to the first city they visited. The solution to the TSP is the shortest route that connects all the cities. 
The KNP involves selecting items to be put in a backpack in such a way that their total value is maximized while their weight remains below the backpack's maximum capacity.

In the TTP, finding the optimum solutions for both subproblems independently does not guarantee finding the optimum for the entire problem, as the subproblems are dependent on one another (the heavier the backpack, the slower the thief moves).

The TTP is solved using three main approaches:
- **the Genetic / Evolutionary Algorithm**:
  - it operates on populations of potential solutions, referred to as individuals, with subsequent populations created based on previous ones using selection, crossover, and mutation operators,
  - each generation is better than the previous one because, according to the idea of evolution, the best-adapted individuals have the best chances of creating offspring.
- **Tabu Search**:
  - it is an improved local search method — it operates on only one solution and analyzes its neighbors,
  - a neighbor is a solution that can be obtained from the current solution by making a single change,
  - starting from a given point, its neighboring points are analyzed, and the best one among them is chosen,
  - a tabu list is used — a list of solutions that have already been visited. If the best solution from the neighborhood is in the tabu list, it will not be selected as the current solution in the next iteration.
- **Simmulated Annealing**:
  - it also is an improved local search method — it operates on only one solution and analyzes its neighbors,
  - a neighbor is a solution that can be obtained from the current solution by making a single change,
  - starting from a given point, its neighboring points are analyzed, and the best one among them is chosen,
  - if the best solution from the neighborhood is better than the current solution, it is selected as the current solution for the next iteration,
  - if the best solution from the neighborhood is worse than the current solution, it is chosen as the current solution for the next iteration with a certain probability - this probability depends on the system temperature,
  - system temperature decreases in subsequent iterations,
  - the higher the temperature, the greater the probability of selecting a worse solution as the current solution for the next iteration.
