#include <omp.h>
#include <stdio.h>
#include <stdlib.h>
#include<time.h>

void block_mat_mul(float **A, float **B, float **C, int size, int block_size){
	float tmp;
    int i, j, k, jj, kk;
    #pragma omp parallel for private(i, j, k, jj, kk, tmp) shared (A, B, C)
	for (jj = 0; jj < size; jj += block_size){
		for (kk = 0; kk < size; kk += block_size){
			for (i = 0; i < size; i++){
				for (j = jj; j < ((jj + block_size) > size ? size : (jj + block_size)); j++){
					tmp = 0.0f;
					for (k = kk; k < ((kk + block_size) > size ? size : (kk + block_size)); k++){
						tmp += A[i][k] * B[k][j];
					}
					C[i][j] += tmp;
				}
			}
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

float** transform_matrix(float* A, int n){
    float ** mat = (float**)malloc(sizeof(float*) * n);
    for(int i = 0; i < n; i++){
        mat[i] = (float*)malloc(sizeof(float) * n); 
    }
    for(int i = 0; i < n; i++){
        for(int j = 0; j < n; j++){
            mat[i][j] = A[i+j];
        }
    }
    return mat;
}

// float* mat_power_n(float** A, int p, int n){
//     float* C = (float*)malloc(sizeof(float*) * n);
//     for(int i = 0; i < n; i++){
//         C[i] = (float*)malloc(sizeof(float) * n);
//     }
//     float* B = (float*)malloc(sizeof(float) * n * n);
//     for(int i = 0; i < n; i++){
//         C[i] = (float*)malloc(sizeof(float) * n);
//     }
//     for(int i = 0; i < n; i++){
//         for(int j = 0)
//         B[i] = A[i];
//     }
//     print_mat(B, n);
//     printf("\n");
//     for(int i = 0; i < p - 1; i++){
//         block_mat_mul(A, B, C, n, b);
//         // #pragma omp parallel for
//         for(int i = 0; i < n*n; i++){
//             B[i] = C[i];
//             C[i] = 0;
//         }
//     }
//     return B;
// }


int main(){
    omp_set_num_threads(16);
    srand(time(0));
    int n = 1024;
    int power = 4;
    float* A = gen_mat(n);
    float **Aa = transform_matrix(A, n);
    // printf("Original matrix is: \n");
    // for(int i = 0; i < n; i++){
    //     for(int j = 0; j < n; j++){
    //         printf("%f ", Aa[i][j]);
    //     }
    //     printf("\n");
    // }
    printf("\n");
    float* B = gen_mat(n);
    float **Bb = transform_matrix(B, n);
    
    // printf("\n");
    for(int i = 0; i < n; i++){
        for(int j = 0; j < n; j++){
            Bb[i][j] = Aa[i][j];
        }
    }
    // for(int i = 0; i < n; i++){
    //     for(int j = 0; j < n; j++){
    //         printf("%f ", Bb[i][j]);
    //     }
    //     printf("\n");
    // }
    // for(int i = 0; i < n; i++){
    //     for(int j = 0; j < n; j++){
    //         printf("%f ", Bb[i][j]);
    //     }
    //     printf("\n");
    // }
    // printf("\n");
    float* C = gen_mat(n);
    float **Cc = transform_matrix(C, n);
    for(int i = 0; i < n; i++){
        for(int j = 0; j < n; j++){
            Cc[i][j] = 0;
        }
    }
    // block_mat_mul(Aa, Bb, Cc, n, 2);
    // for(int i = 0; i < n; i++){
    //     for(int j = 0; j < n; j++){
    //         printf("%f ", Cc[i][j]);
    //     }
    //     printf("\n");
    // }
    double starttime = omp_get_wtime(); 
    for(int p = 0; p < power - 1; p++){
        block_mat_mul(Aa, Bb, Cc, n, 2);
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                Bb[i][j] = Cc[i][j];
                Cc[i][j] = 0;
            }
        }
    }
    double endtime = omp_get_wtime();
    // for(int i = 0; i < p; i++){
    //     block_mat_mul(Aa, Bb, Cc, n, 2);
    //     for(int j = 0; j < n; j++){
    //         for(int k = 0; k < n; k++){
    //             Bb[j][k] = Cc[j][k];
    //         }
    //     }
    //     for(int i = 0; i < n; i++){
    //         for(int j = 0; j < n; j++){
    //             printf("%f ", Bb[i][j]);
    //             Cc[i][j] = 0;
    //         }
    //     }
    //     printf("\n");
    // }

    // printf("\n");
    // printf("matrix power %d is: \n", power);
    // for(int i = 0; i < n; i++){
    //     for(int j = 0; j < n; j++){
    //         printf("%f ", Bb[i][j]);
    //     }
    //     printf("\n");
    // }
    printf("Time taken is %f", endtime - starttime);
    printf("\n");
}