`timescale 1ns/1ns
module adder_tb();
//localparam CLKPERIOD = 20;
reg [3:0] a,b;
reg [3:0] sum;
reg clk;

adder DUT(.clk(clk), .a(a), .b(b), .sum(sum));

initial begin: clk_generation
  //clk = 1'b0; forever #(CLKPERIOD/2) clk = ~clk;
  clk = 1'b0; forever #10 clk = ~clk;
end

initial begin: adder_stim
  a = 3'b1; b = 3'b0;
  forever begin @(negedge clk);
    a = a + 3'd2;
    b = b + 3'd3;
  end
end

//initial #360 $stop;
initial begin
  $display("\t\t Time     a     b     sum");
  $monitor($time,,,,,a,,,,,b,,,,,sum);
  #360 $stop;
end

endmodule
