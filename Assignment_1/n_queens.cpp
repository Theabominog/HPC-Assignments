#define N 20
#include <stdbool.h>
#include <stdio.h>
#include<stdlib.h>
#include<omp.h>
#include<time.h>

int ld[30] = { 0 };
int rd[30] = { 0 };
int col_num[30] = { 0 };

void printBoard(int board[N][N]){
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++)
            printf(" %d ", board[i][j]);
        printf("\n");
    }
}

bool check_recursive(int board[N][N], int col){
        // printf("%d\n", col);
    if (col >= N)

        return true;
    bool flag = false;
    // #pragma omp parallel for
    for (int i = 0; i < N; i++) {

        if ((ld[i - col + N - 1] != 1 && rd[i + col] != 1) && col_num[i] != 1) {
            board[i][col] = 1;
            ld[i - col + N - 1] = rd[i + col] = col_num[i] = 1;
            // #pragma omp critical
            if (check_recursive(board, col + 1))
                flag = true; // set flag as cannot have several breaks in omp area
            
            else{board[i][col] = 0;
            ld[i - col + N - 1] = rd[i + col] = col_num[i] = 0;}
        }
    }
    return flag; //return flag value at end instead of as soon as condition is met.
}

  
int main(){
    omp_set_num_threads(16);
    double startTime = omp_get_wtime();

    int board[N][N] = {0};
    // sleep(3);
    
  
    if (check_recursive(board, 0) == false) {
        printf("Solution does not exist");
    }
    else printBoard(board);
    double endTime = omp_get_wtime();
    // gettimeofday(&new_time, NULL);
    printf("time - %f\n", endTime - startTime);

    return 0;
}