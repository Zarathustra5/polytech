module lab6_1 #(parameter W = 18)( input [W-1:0] a, b, output reg [W-1:0] max, min);

task Tsort(input [W-1:0] one, two, output reg [W-1:0] max, min);
	begin
		if (one>two) begin
			max <= one;
			min <= two;
		end	
		else begin
			min <= one;
			max <= two;
		end
	end
endtask

always @*
		begin
			Tsort(a, b, max, min);
		end
endmodule		