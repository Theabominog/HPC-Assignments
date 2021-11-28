#include <omp.h>
#include <stdio.h>
#include <stdlib.h>
#include<time.h>


void print_mat(float* mat, int n){
    for(int i = 0; i < n*n; i++){
        if(i % n == 0){
            printf("\n");
        }
        printf("%f ", mat[i]);
        
    }
}

void mat_mul(float* A, float* B, float* C, int n){
    int i,j,l;
    float sum;

    #pragma omp  parallel for private (i,j,l) shared (A, B, C)
    for(i = 0; i < n; ++i){
        // printf("%d \n", i);
        for(j = 0; j < n; ++j){
            for(l = 0; l < n; ++l){
                C[i*n + j]  = C[i*n + j]  + (A[i*n + l] * B[j + l*n]);
            }
            // C[i*n + j] = sum;
            // sum = 0;
        }
    }
}

float* gen_mat(int n){
    float* mat = (float*) calloc(n*n, sizeof(float));
    for(int i = 0; i < n*n; i++){
        mat[i] = (float)rand()/RAND_MAX;
    }
    return mat;
}



float* mat_power_n(float* A, int p, int n){
    float* C = (float*)malloc(sizeof(float) * n * n);
    float* B = (float*)malloc(sizeof(float) * n * n);
    for(int i = 0; i < n*n; i++){
        B[i] = A[i];
    }
    // print_mat(B, n);
    // printf("\n");
    for(int i = 0; i < p - 1; i++){
        // printf("%d \n", i);
        mat_mul(A, B, C, n);
        // #pragma omp parallel for
        for(int i = 0; i < n*n; i++){
            B[i] = C[i];
            C[i] = 0;
        }
    }
    return B;
}



int main(){
    srand(time(0));
    int t = 16;
    int n = 512;
    int p = 10;
    omp_set_num_threads(t);
    float* A = gen_mat(n);
    // print_mat(A, n);
    // printf("\n");
    // float* B = gen_mat(n);
    // print_mat(B, n);
    // printf("\n");
    // float* C = (float*)malloc(sizeof(float) * n * n);
    // mat_mul(A, B, C, n);
    double starttime = omp_get_wtime();
    float* C = mat_power_n(A, p, n);
    double endtime = omp_get_wtime();
    // print_mat(C, n);
    // printf("\n");
    printf("Time taken is %f \n", endtime - starttime);
    // printf("\n");
}