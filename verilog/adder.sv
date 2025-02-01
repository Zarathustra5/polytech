//`timescale 1 ns/100ps
`timescale 1 ns/1ns
module adder(
  input clk, [3:0]a, [3:0]b,
  output reg [3:0] sum
);

initial sum<=4'd0;
//always @(posedge clk)
always @(negedge clk)
sum <= a + b;

endmodule
