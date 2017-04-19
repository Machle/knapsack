import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;


/**
 * Created by tano on 16.11.16.
 */


public class Main {

    public static ArrayList<ArrayList<Integer>> individuals = new ArrayList<>();

    private static ArrayList<ArrayList<Integer>> make_population(int n){

        ArrayList<ArrayList<Integer>> P = new ArrayList<>();

        Random r = new Random();
        for(int i=0;i<n;i++) {
            P.add(new ArrayList<Integer>());

            for (int j = 0; j < n; j++) {
                boolean temp = r.nextBoolean();
                if (temp == true) {
                    P.get(i).add(1);
                } else {
                    P.get(i).add(0);
                }

            }
            if(individuals.contains(P.get(i))){
                P.remove(i);
                i--;
            } else {
                individuals.add(P.get(i));
            }
        }


        return P;
    }

    public static int[] calc_fitness(ArrayList<ArrayList<Integer>> Population, int[] fitness){

        int[] calculated_fitness = new int[fitness.length];
        for(int i = 0; i<Population.size();i++){
            int indiv_fitness = 0;
            for(int j = 0; j<fitness.length;j++){
                indiv_fitness += fitness[j]*Population.get(i).get(j);
            }
            calculated_fitness[i] = indiv_fitness;
        }

        return calculated_fitness;

    }

    public static int[] calc_weight(ArrayList<ArrayList<Integer>> Population,
                                        int[] fitness, int[] weight, int max_weight){

        int[] calculated_weight = new int[weight.length];
        for(int i = 0; i<Population.size();i++){
            int indiv_weight = 0;
            for(int j = 0; j<weight.length;j++){
                indiv_weight += weight[j]*Population.get(i).get(j);
            }
            calculated_weight[i] = indiv_weight;

            //If weight of individual is over our max_weight
            //we set his fitness to 0
            if(calculated_weight[i] > max_weight)
                fitness[i] = 0;
        }

        return calculated_weight;
    }

    public static int get_fittest(ArrayList<Integer> fitness_of_population){
        int fittest = fitness_of_population.get(0);
        int index = 0;
        for(int i = 0;i<fitness_of_population.size();i++){
            if(fittest<fitness_of_population.get(i)) {
                fittest = fitness_of_population.get(i);
                index = i;
            }
        }
        return index;

    }


    public static ArrayList<Integer> crossover (ArrayList<Integer> indiv1, ArrayList<Integer> indiv2){
        ArrayList<Integer> new_indiv = new ArrayList<>();
        for(int i=0; i<indiv1.size();i++){
            //uniform
            // 0.5 is the uniform rate
            if(Math.random() < 0.5){

                new_indiv.add(i,indiv1.get(i));

            } else{

                new_indiv.add(i,indiv2.get(i));

            }
        }

        return new_indiv;
    }

    public static void mutate(ArrayList<Integer> indiv){

        for(int i=0;i<indiv.size();i++) {
            Random r = new Random();

            // 0.01 is the mutation rate
            if(Math.random() <= 0.01){

                //Create a random gene
                boolean temp = r.nextBoolean();
                if (temp == true) {
                    indiv.set(i, 1);
                } else {
                    indiv.set(i,0);
                }

            }
        }
    }


    public static void main(String[] args) {

        //# of individuals
        System.out.print("Enter the number of individuals:");
        Scanner in = new Scanner(System.in);
        int number1 = in.nextInt();
        int n = (int) number1;

        System.out.print("Enter the max weight of the knapsack:");
        int number = in.nextInt();
        int max_weight = (int) number;

        int[] fitness = new int[n];
        int[] weight = new int[n];

        System.out.print("Enter the number of individuals to be removed: ");

        //# of individuals to remove
        number = in.nextInt();
        int r = (int) number;

        System.out.print("Enter the number of individuals to be mutated: ");

        //# of individuals to mutate
        number = in.nextInt();
        int m = (int) number;


        System.out.println("Enter fitness and weight: ");

        for(int i = 0; i<n;i++) {
            int fit = in.nextInt();
            fitness[i] = (int) fit;
            int w = in.nextInt();
            weight[i] = (int) w;
        }



        // we generate n random individuals
        ArrayList<ArrayList<Integer>> P = make_population(n);

        int[] fitness_of_population = new int[P.size()];
        int max_fitness = 0;

        fitness_of_population = calc_fitness(P,fitness);
        ArrayList<Integer> fit = new ArrayList<>();
        for(int i = 0; i<fitness_of_population.length;i++){
            fit.add(i,fitness_of_population[i]);
        }

        int s=n*100;
        while(s-- > 0){

            ArrayList<ArrayList<Integer>> new_population = new ArrayList<>();

            //calculates the fitness of our population

            fitness_of_population = calc_fitness(P,fitness);

            //calculates the weight
            //if an individual has more weight than max_weight
            //it turns it fitness to 0
            calc_weight(P,fitness_of_population,weight,max_weight);



            fit.clear();
            for(int i = 0; i<fitness_of_population.length;i++){
                fit.add(i,fitness_of_population[i]);
            }

            int index = get_fittest(fit);
            if(max_fitness <= fitness_of_population[index]){
                max_fitness = fitness_of_population[index];
            }

            //selection
            int k = n-r;
            while(k!=0){
                //choosing n-r potential individuals
                //and then we put them in the new population
                index = get_fittest(fit);
                fit.remove(index);


                new_population.add(P.get(index));

                k--;
            }

            //We choose r/2 tuples of parents
            //and we perform crossover
            k = r/2;
            while(k!=0){
                Random rand = new Random();
                int randomNum = rand.nextInt((P.size()));
                int randomNum2 = rand.nextInt((P.size()));
                while(randomNum2!=randomNum) {
                    randomNum2 = rand.nextInt((P.size()));
                }
                ArrayList<Integer> new_indiv = crossover(P.get(randomNum),P.get(randomNum2));
                new_population.add(new_indiv);
                k--;

            }


            //mutation
            //We choose m random individuals from our new population
            //and we mutate them randomly
            k = m;
            ArrayList<Integer> to_mutate = new ArrayList<Integer>(m);
            while(k!=0){
                Random rand = new Random();
                int randnum;
                do{
                    randnum = rand.nextInt(new_population.size());
                } while(to_mutate.contains(randnum));
                to_mutate.add(randnum);
                k--;
            }

            for(int j =0;j<m;j++){
                mutate(new_population.get(to_mutate.get(j)));
            }



            P = new_population;
        }

        System.out.println(max_fitness);
    }
}
