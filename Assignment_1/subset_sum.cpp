#include <bits/stdc++.h>
#include<omp.h>
#include<time.h>
#include<stdlib.h>
using namespace std;

int** subset;
 
void find_Subsets(int arr[], int n, int sum);
void print_Subsets(int arr[], int i, int sum, vector<int>& p);

int main(){
    omp_set_num_threads(16);
    srand(time(0));
    int n = 70;
    int sum = 23142;
    int temp;
    int *arr = new int[n];
    for(int i = 0; i < n; i++){
        arr[i] = i;
        // printf("%d ", arr[i]);
    }
    printf("\n");
    double starttime = omp_get_wtime();
    find_Subsets(arr, n, sum);
    double endtime = omp_get_wtime();
    printf("Time taken is %f \n", endtime - starttime);
    return 0;
}

void print_Subsets(int arr[], int i, int sum, vector<int>& p){
    if (i == 0 && sum != 0 && subset[0][sum]){
        p.insert(p.begin(), arr[i]);
            if (arr[i] == sum){
                for(int i = 0; i < p.size(); i++){
                    printf("%d ", p[i]);
                }
                printf("\n");
            }
        return;
    }

    if (i == 0 && sum == 0){
        for(int i = 0; i < p.size(); i++){
            printf("%d ", p[i]);
        }
        printf("\n");
        return;
    }

    if (subset[i-1][sum]){
        vector<int> b = p;
        print_Subsets(arr, i-1, sum, b);
    }
    
    if (sum >= arr[i] && subset[i-1][sum-arr[i]])
    {
        p.insert(p.begin(), arr[i]);
        print_Subsets(arr, i-1, sum-arr[i], p);
    }
}

void find_Subsets(int arr[], int n, int sum){
    if (n == 0 || sum < 0)
       return;

    subset = new int*[n];
    for (int i=0; i<n; ++i)
    {
        subset[i] = new int[sum + 1];
        subset[i][0] = 1;
    }
    // int **subset = (int**)malloc(sizeof(int*) * n);
    // for(int i = 0; i < n; ++i){
    //     subset[i] = (int*)malloc(sizeof(int) * (sum + 1));
    //     subset[i][0] = 1;
    // } 

    if (arr[0] <= sum)
       subset[0][arr[0]] = 1;

        #pragma omp parallel for
    for (int i = 1; i < n; ++i)

        for (int j = 0; j < sum + 1; ++j)
            subset[i][j] = (arr[i] <= j) ? subset[i-1][j] ||
                                       subset[i-1][j-arr[i]]
                                     : subset[i - 1][j];
    if (subset[n-1][sum] == 0)
    {
        printf("There are no subsets with sum %d\n", sum);
        return;
    }
    printf("Subset is found\n");
    vector<int> p;
    //uncomment to see subsets
    // print_Subsets(arr, n-1, sum, p);  

}

