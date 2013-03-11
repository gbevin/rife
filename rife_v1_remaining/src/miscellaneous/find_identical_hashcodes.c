#include <string.h>

int main()
{
	int size = 4;
	
	char low = '0';
	char high = 'z';

	char *p_target = (char*)malloc((size+1)*sizeof(char));
	char *p_buffer1 = (char*)malloc((size+1)*sizeof(char));
	char *p_buffer2 = (char*)malloc((size+1)*sizeof(char));
	
	memset(p_target, high, size*sizeof(char));
	p_target[size] = 0;
	
	memset(p_buffer1, low, size*sizeof(char));
	p_buffer1[size] = 0;
	
	memset(p_buffer1, low, size*sizeof(char));
	p_buffer2[size] = 0;
	
	int hashcode1 = 0;
	int hashcode2 = 0;
	int i = 0;
	int j = 0;
	
	do
	{
		hashcode1 = hashcode(p_buffer1);
		/*printf("p_buffer1 : %s\n", p_buffer1);*/
		
		memset(p_buffer2, low, size*sizeof(char));
		
		do
		{
			hashcode2 = hashcode(p_buffer2);
			/*printf("p_buffer2 : %s\n", p_buffer2);*/

			if (hashcode1 == hashcode2 &&
				strcmp(p_buffer1, p_buffer2) != 0)
			{
				printf("%d = '%s' %d = '%s'\n", hashcode1, p_buffer1, hashcode2, p_buffer2);
			}
		
			for (i = 0; i < size; i++)
			{
				if (p_buffer2[i] != high)
				{
					p_buffer2[i] = p_buffer2[i]+1;
					for (j = 0; j < i; j++)
					{
						p_buffer2[j] = low;
					}
				
					break;
				}
			}
		
		}
		while (strcmp(p_buffer2, p_target) != 0);

		for (i = 0; i < size; i++)
		{
			if (p_buffer1[i] != high)
			{
				p_buffer1[i] = p_buffer1[i]+1;
				for (j = 0; j < i; j++)
				{
					p_buffer1[j] = low;
				}
				
				break;
			}
		}
		
	}
	while (strcmp(p_buffer1, p_target) != 0);
	
	free(p_buffer1);
	free(p_buffer2);
	free(p_target);
}

int hashcode(char *pString)
{
	int h = 0;
	
	char *val = pString;
	int len = strlen(pString);
	int i = 0;
	
	for (i = 0; i < len; i++)
	{
		h = 31*h + *val++;
	}
	
	return h;
}
