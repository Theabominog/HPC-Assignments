#include<stdio.h>
#include<omp.h>
#include<stdlib.h>
#include<time.h>

float** gen_mat(int a, int b){
    float** mat = (float**)malloc(sizeof(float*) * a);
    for(int i = 0; i < a; i++){
        mat[i] = (float*)malloc(sizeof(float) * b);
    }
    for(int i = 0; i < a; i++){
        for(int j = 0; j < b; j++){
            mat[i][j] = (float)rand()/RAND_MAX;
        }
    }
    return mat;
}

float** gen_mat_zeros(int a, int b){
    float** mat = (float**)malloc(sizeof(float*) * a);
    for(int i = 0; i < a; i++){
        mat[i] = (float*)malloc(sizeof(float) * b);
    }
    for(int i = 0; i < a; i++){
        for(int j = 0; j < b; j++){
            mat[i][j] = 0;
        }
    }
    return mat;
}

float** mat_mul(float** A, float** B, int a, int b, int c){
    float** C = gen_mat_zeros(a, c);
    int sum = 0;
    #pragma omp parallel for
    for(int i = 0; i < a; i++){
        for(int j = 0; j < c; j++){
            for(int k = 0; k < b; k++){
                C[i][j] += A[i][k] * B[k][j]; 
            }
        }
    }
    return C;
}


int main(){
    // srand(time(0));
    // int n = 4;

    // int sizes[] = {5, 12, 8, 7};
    // float*** mats = (float***)malloc(sizeof(float**) * n);
    // for(int i = 0; i < n - 1; i++){
    //     mats[i] = gen_mat(sizes[i], sizes[i + 1]);
    // }
    // for(int i = 0; i < 5; i++){
    //     for(int j = 0; j < 12; j++){
    //         printf("%f ", mats[0][i][j]);
    //     }
    //     printf("\n");
    // }
    //     printf("\n");

    // for(int i = 0; i < 12; i++){
    //     for(int j = 0; j < 8; j++){
    //         printf("%f ", mats[1][i][j]);
    //     }
    //     printf("\n");
    // }
    //     printf("\n");
    // for(int i = 0; i < 8; i++){
    //     for(int j = 0; j < 7; j++){
    //         printf("%f ", mats[2][i][j]);
    //     }
    //     printf("\n");
    // }
    //     printf("\n");

    // float** temp_matrix;
    // float** fin_matrix;
    // temp_matrix = gen_mat_zeros(sizes[0], sizes[1]);
    // for(int i = 0; i < sizes[0]; i++){
    //     for(int j = 0; j < sizes[1]; j++){
    //         temp_matrix[i][j] = mats[0][i][j];
    //     }
    // }
    // fin_matrix = gen_mat_zeros(sizes[0], sizes[2]);
    // // int p =4;
    // for(int i = 0; i < n - 2; i++){
    //     mat_mul(temp_matrix, mats[i+1], fin_matrix, sizes[0], sizes[i+1], sizes[i+2]);
    //     // printf("%f \n", fin_matrix[1][1]);
    //     free(temp_matrix);
    //     temp_matrix = gen_mat_zeros(sizes[0], sizes[i+2]);
    //     printf("temp matrix size is %d x %d\n", sizes[0], sizes[i+2]);
    //     for(int i = 0; i < sizes[0]; i++){
    //         for(int j = 0; j < sizes[i+2]; j++){
    //             temp_matrix[i][j] = fin_matrix[i][j];
    //         }
    //     }
    //     for(int i = 0; i < sizes[0]; i++){
    //         for(int j = 0; j < sizes[i+2]; j++){
    //             printf("%f ", temp_matrix[i][j]);
    //     }
    //     printf("\n");
    // }
    //     free(fin_matrix);

    //     fin_matrix = gen_mat_zeros(sizes[0], sizes[i+3]);
    //     printf("fin matrix size is %d x %d\n", sizes[0], sizes[i+3]);        
    // }

    // for(int i = 0; i < 5; i++){
    //     for(int j = 0; j < 8; j++){
    //         printf("%f ", temp_matrix[i][j]);
    //     }
    //     printf("\n");
    // }
    // free(temp_matrix);

    omp_set_num_threads(16);
    srand(time(0));
    int size = 15;
    int a[] = {43, 53, 37, 29, 31, 42, 34, 32, 35, 43, 45, 56, 54, 43, 65};

    float*** mats = (float***) malloc(sizeof(float**) * (size - 1));
    for (int i = 0; i < size - 1; i++) {
        mats[i] = (float**) malloc(sizeof(float*) * a[i]);
        for (int j = 0; j < a[i]; j++) {
            mats[i][j] = (float*) malloc(sizeof(float) * a[i+1]);
            for (int k = 0; k < a[i+1]; k++) {
                mats[i][j][k] = (float)rand() / RAND_MAX;
            }
        }
    }
    double starttime = omp_get_wtime();
    for (int i = 0; i < size - 2; i++) {
        mats[i + 1] = mat_mul(mats[i], mats[i+1], a[i], a[i+1], a[i+2]);
        a[i+1] = a[i];
    }
    // for(int i = 0; i < a[0]; i++){
    //     for(int j = 0; j < a[size - 1]; j++){
    //         printf("%f ", mats[size - 2][i][j]);
    //     }
    //     printf("\n");
    // }
    double endtime = omp_get_wtime();
    printf("time taken is %f \n", endtime - starttime);
}
